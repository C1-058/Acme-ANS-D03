
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
public class ManagerLegCreateService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("masterId", int.class);

		flight = this.repository.findFlightById(flightId);

		if (flight != null) {
			int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			status = flight.getManager().getId() == managerId && flight.getDraftMode();
		} else
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int masterId = super.getRequest().getData("masterId", int.class);
		super.getResponse().addGlobal("masterId", masterId);

		Flight flight = this.repository.findFlightById(masterId);

		Leg leg = new Leg();
		leg.setFlight(flight);
		leg.setStatus(LegStatus.ON_TIME);
		leg.setDraftMode(true);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "flightNumberDigits", "departure", "arrival", "status", "departureAirport", "arrivalAirport", "aircraft");
	}

	@Override
	public void validate(final Leg leg) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);

		if (confirmation) {
			String newFlightNumberDigits = super.getRequest().getData("flightNumberDigits", String.class);
			Optional<Leg> currentLegWithFlightNumberDigits = this.repository.findLegByFlightNumberDigits(newFlightNumberDigits);
			int legId = super.getRequest().getData("id", int.class);

			Airport departureAirportCode = super.getRequest().getData("departureAirport", Airport.class);
			Airport arrivalAirportCode = super.getRequest().getData("arrivalAirport", Airport.class);

			Date departure = super.getRequest().getData("departure", Date.class);
			Date arrival = super.getRequest().getData("arrival", Date.class);

			List<String> numbersAtTheSameTime = this.repository.findLegsByMomentBracket(departure, arrival, LegStatus.ON_TIME, LegStatus.DELAYED).stream().filter(x -> x.getId() != legId).map(x -> x.getAircraft().getRegistrationNumber()).toList();
			Aircraft newAircraft = super.getRequest().getData("aircraft", Aircraft.class);
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

		dataset.put("statusChoices", statusChoices);
		dataset.put("flights", flightChoices);
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("departureAirports", departureAirportChoices);
		dataset.put("arrivalAirports", arrivalAirportChoices);

		super.getResponse().addGlobal("masterId", leg.getFlight().getId());
		super.getResponse().addData(dataset);
	}

}
