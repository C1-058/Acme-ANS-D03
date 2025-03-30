
package acme.features.administrator.airline;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineType;

@GuiService
public class AdministratorAirlineUpdateService extends AbstractGuiService<Administrator, Airline> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirlineRepository repository;

	// AbstractGuiService interfaced ------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airline airline;
		int id;

		id = super.getRequest().getData("id", int.class);
		airline = this.repository.findAirlineById(id);

		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "iataCode", "website", "type", "foundation", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airline airline) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		if (confirmation) {
			String newIataCode = super.getRequest().getData("iataCode", String.class);
			int airlineId = super.getRequest().getData("id", int.class);
			Date newDate = super.getRequest().getData("foundation", Date.class);
			Date currentDate = new Date();
			Optional<Airline> currentAirlineWithIataCode = this.repository.findAirlineByIataCode(newIataCode);

			if (currentAirlineWithIataCode.isPresent() && currentAirlineWithIataCode.get().getId() != airlineId)
				super.state(false, "iataCode", "administrator.airline.update.existingIataCode");
			if (newDate.after(currentDate))
				super.state(false, "foundation", "administrator.airline.update.dateInTheFuture");
		} else
			super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airline airline) {
		this.repository.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(AirlineType.class, airline.getType());

		dataset = super.unbindObject(airline, "name", "iataCode", "website", "type", "foundation", "email", "phoneNumber");
		dataset.put("types", choices);

		super.getResponse().addData(dataset);
	}

}
