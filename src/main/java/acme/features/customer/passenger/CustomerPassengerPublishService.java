
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerPublishService extends AbstractGuiService<Customer, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractService<Customer, Passenger> -------------------------------------


	@Override
	public void authorise() {

		boolean status;
		int masterId;
		int customerId;
		Passenger passenger;
		Customer customer;

		masterId = super.getRequest().getData("id", int.class);
		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		passenger = this.repository.findPassengerById(masterId);
		customer = passenger == null ? null : passenger.getCustomer();

		status = customer != null && super.getRequest().getPrincipal().hasRealm(customer) && customerId == customer.getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Passenger passenger;
		int id;

		id = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(id);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		Customer customer;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		super.bindObject(passenger, "fullName", "email", "passportNumber", //
			"dateOfBirth", "specialNeeds");
		passenger.setCustomer(customer);
	}

	@Override
	public void validate(final Passenger passenger) {

		boolean uniquePassportNumber;

		Passenger existingPassenger;

		existingPassenger = this.repository.findPassengerByPassportNumber(passenger.getPassportNumber());
		uniquePassportNumber = existingPassenger == null || existingPassenger.equals(passenger);

		super.state(uniquePassportNumber, "passportNumber", "acme.validation.passenger.duplicated-passportNumber.message");
	}

	@Override
	public void perform(final Passenger passenger) {
		passenger.setDraftMode(false);
		this.repository.save(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", //
			"dateOfBirth", "specialNeeds", "draftMode");

		super.getResponse().addData(dataset);
	}

}
