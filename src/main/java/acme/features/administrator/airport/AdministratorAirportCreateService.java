
package acme.features.administrator.airport;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.airport.OperationalScope;

@GuiService
public class AdministratorAirportCreateService extends AbstractGuiService<Administrator, Airport> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirportRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Airport airport;

		airport = new Airport();

		super.getBuffer().addData(airport);
	}

	@Override
	public void bind(final Airport airport) {
		super.bindObject(airport, "name", "iataCode", "operationalScope", "city", "country", "website", "emailAddress", "contactPhoneNumber");
	}

	@Override
	public void validate(final Airport airport) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		if (confirmation) {
			String newIataCode = super.getRequest().getData("iataCode", String.class);
			int airportId = super.getRequest().getData("id", int.class);
			Optional<Airport> currentAirportWithIataCode = this.repository.findAirportByIataCode(newIataCode);

			if (currentAirportWithIataCode.isPresent() && currentAirportWithIataCode.get().getId() != airportId)
				super.state(false, "iataCode", "administrator.airport.create.existingIataCode");

		} else
			super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airport airport) {
		this.repository.save(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		Dataset dataset;
		SelectChoices choices;

		choices = SelectChoices.from(OperationalScope.class, airport.getOperationalScope());

		dataset = super.unbindObject(airport, "name", "iataCode", "operationalScope", "city", "country", "website", "emailAddress", "contactPhoneNumber");
		dataset.put("operationalScopes", choices);

		super.getResponse().addData(dataset);
	}

}
