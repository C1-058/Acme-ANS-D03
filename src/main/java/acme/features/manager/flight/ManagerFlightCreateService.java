
package acme.features.manager.flight;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightCreateService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Manager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight flight;
		flight = new Flight();
		int managerId;

		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Manager manager = this.repository.findManagerById(managerId);
		flight.setManager(manager);
		flight.setDraftMode(true);
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "cost", "requiresSelfTransfer", "description");
	}

	@Override
	public void validate(final Flight flight) {
		boolean confirmation;
		List<String> acceptedCurrencies = List.of("EUR", "USD", "GBP");

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		Money flightCost = flight.getCost();

		if (flightCost != null)
			super.state(acceptedCurrencies.contains(flightCost.getCurrency()), "cost", "manager.flight.form.wrongCurrency");

		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Flight flight) {
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;
		dataset = super.unbindObject(flight, "tag", "cost", "requiresSelfTransfer", "description", "draftMode");
		super.getResponse().addData(dataset);
	}

}
