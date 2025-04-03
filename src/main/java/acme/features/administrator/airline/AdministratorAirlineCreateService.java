
package acme.features.administrator.airline;

import java.util.Date;
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
public class AdministratorAirlineCreateService extends AbstractGuiService<Administrator, Airline> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirlineRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airline airline;

		airline = new Airline();
		airline.setDraftMode(true);

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
			Date newDate = super.getRequest().getData("foundation", Date.class);
			Optional<Airline> currentAirlineWithIataCode = this.repository.findAirlineByIataCode(newIataCode);

			if (currentAirlineWithIataCode.isPresent())
				super.state(false, "iataCode", "administrator.airline.update.existingIataCode");
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
		dataset.put("draftMode", false);
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);
	}

}
