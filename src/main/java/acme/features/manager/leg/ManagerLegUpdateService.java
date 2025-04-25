
package acme.features.manager.leg;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.entities.flight.Leg;
import acme.entities.flight.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegUpdateService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int legId;
		Leg leg;

		legId = super.getRequest().getData("id", int.class);

		leg = this.repository.findLegById(legId);

		if (leg != null) {
			int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			status = leg.getFlight().getManager().getId() == managerId && leg.getDraftMode();
		} else
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int id;

		id = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "flightNumberDigits", "departure", "arrival", "status");
	}

	@Override
	public void validate(final Leg leg) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);

		if (confirmation) {
			int legId = super.getRequest().getData("id", int.class);
			String newFlightNumberDigits = super.getRequest().getData("flightNumberDigits", String.class);
			Airport departureAirportCode = super.getRequest().getData("departureAirport", Airport.class);
			Airport arrivalAirportCode = super.getRequest().getData("arrivalAirport", Airport.class);
			Date departure = super.getRequest().getData("departure", Date.class);
			Date arrival = super.getRequest().getData("arrival", Date.class);
			Aircraft newAircraft = super.getRequest().getData("aircraft", Aircraft.class);

			if (newFlightNumberDigits != null && departureAirportCode != null && arrivalAirportCode != null && departure != null && arrival != null && newAircraft != null) {
				Optional<Leg> currentLegWithFlightNumberDigits = this.repository.findLegByFlightNumberDigits(newFlightNumberDigits);

				List<String> numbersAtTheSameTime = this.repository.findLegsByMomentBracket(departure, arrival, LegStatus.ON_TIME, LegStatus.DELAYED).stream().filter(x -> x.getId() != legId).map(x -> x.getAircraft().getRegistrationNumber()).toList();

				String newAircraftNumber = newAircraft.getRegistrationNumber();

				if (currentLegWithFlightNumberDigits.isPresent() && currentLegWithFlightNumberDigits.get().getId() != legId)
					super.state(false, "flightNumberDigits", "manager.leg.existingFlightNumberDigits");

				if (departureAirportCode.equals(arrivalAirportCode))
					super.state(false, "*", "manager.leg.sameAirports");

				if (MomentHelper.isPast(departure))
					super.state(false, "departure", "manager.leg.departureInThePast");

				if (arrival.before(departure))
					super.state(false, "arrival", "manager.leg.arrivalBeforeDeparture");

				if (numbersAtTheSameTime.contains(newAircraftNumber))
					super.state(false, "aircraft", "manager.leg.usedAircraft");
			} else
				super.state(false, "*", "manager.leg.form.null");

			super.state(leg.getDraftMode(), "*", "manager.leg.deletePublishedLeg");
		} else
			super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;
		SelectChoices flightChoices;
		SelectChoices departureAirportChoices;
		SelectChoices arrivalAirportChoices;
		SelectChoices aircraftChoices;
		SelectChoices statusChoices;

		List<Flight> flights = this.repository.findFlightsByManager(leg.getFlight().getManager().getId());
		flightChoices = SelectChoices.from(flights, "tag", leg.getFlight());

		List<Airport> airports = this.repository.findAllAirports();
		departureAirportChoices = SelectChoices.from(airports, "iataCode", leg.getDepartureAirport());
		arrivalAirportChoices = SelectChoices.from(airports, "iataCode", leg.getArrivalAirport());

		List<Aircraft> aircrafts = this.repository.findAllAircrafts();
		aircraftChoices = SelectChoices.from(aircrafts, "registrationNumber", leg.getAircraft());

		statusChoices = SelectChoices.from(LegStatus.class, leg.getStatus());

		dataset = super.unbindObject(leg, "flightNumberDigits", "departure", "arrival", "status", "draftMode");

		dataset.put("flightNumber", leg.getFlightNumber());
		//dataset.put("duration", leg.getDuration());
		dataset.put("statusChoices", statusChoices);
		dataset.put("published", !leg.getDraftMode());
		dataset.put("flight", leg.getFlight().getTag());
		dataset.put("flights", flightChoices);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("departureAirport", departureAirportChoices.getSelected().getKey());
		dataset.put("departureAirports", departureAirportChoices);
		dataset.put("arrivalAirport", arrivalAirportChoices.getSelected().getKey());
		dataset.put("arrivalAirports", arrivalAirportChoices);

		super.getResponse().addData(dataset);
	}

}
