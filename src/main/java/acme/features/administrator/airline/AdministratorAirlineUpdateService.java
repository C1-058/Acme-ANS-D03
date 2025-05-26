
package acme.features.administrator.airline;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
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

		if (super.getRequest().getMethod().equals("GET"))
			status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		else {
			List<String> existingTypes = List.of("LUXURY", "STANDARD", "LOW_COST", "0");
			String type = super.getRequest().getData("type", String.class);
			status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class) && existingTypes.contains(type);
		}

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
			String newIataCode = airline.getIataCode();
			int airlineId = super.getRequest().getData("id", int.class);
			Date newDate = airline.getFoundation();
			Optional<Airline> currentAirlineWithIataCode = this.repository.findAirlineByIataCode(newIataCode);

			if (currentAirlineWithIataCode.isPresent() && currentAirlineWithIataCode.get().getId() != airlineId)
				super.state(false, "iataCode", "administrator.airline.update.existingIataCode");
			if (newDate != null)
				if (MomentHelper.isFuture(newDate))
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
