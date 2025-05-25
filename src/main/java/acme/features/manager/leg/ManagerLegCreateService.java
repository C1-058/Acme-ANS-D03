
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
		String status_;
		int aircraftId;
		Aircraft aircraft;
		int arrivalAirportId;
		Airport arrivalAirport;
		int departureAirportId;
		Airport departureAirport;
		boolean aircraftStatus;
		boolean departureAirportStatus;
		boolean arrivalAirportStatus;
		boolean statusStatus;

		flightId = super.getRequest().getData("masterId", int.class);
		flight = this.repository.findFlightById(flightId);

		if (super.getRequest().getMethod().equals("GET"))
			status = flight != null && flight.getDraftMode() && super.getRequest().getPrincipal().hasRealm(flight.getManager());
		else {
			status_ = super.getRequest().getData("status", String.class);
			List<String> statusList = List.of("ON_TIME", "DELAYED", "CANCELLED", "LANDED", "0");
			statusStatus = statusList.contains(status_);

			aircraftId = super.getRequest().getData("aircraft", int.class);
			aircraft = this.repository.findAircraftById(aircraftId);
			aircraftStatus = aircraftId == 0 || aircraft != null;
			if (aircraft != null)
				aircraftStatus = aircraftStatus && aircraft.getAirline().getIataCode().equals(flight.getManager().getAirline().getIataCode());

			departureAirportId = super.getRequest().getData("departureAirport", int.class);
			departureAirport = this.repository.findAirportById(departureAirportId);
			departureAirportStatus = departureAirportId == 0 || departureAirport != null;

			arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
			arrivalAirport = this.repository.findAirportById(arrivalAirportId);
			arrivalAirportStatus = arrivalAirportId == 0 || arrivalAirport != null;

			status = flight != null && flight.getDraftMode() && super.getRequest().getPrincipal().hasRealm(flight.getManager()) && aircraftStatus && departureAirportStatus && arrivalAirportStatus && statusStatus;
		}
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
			int legId = super.getRequest().getData("id", int.class);
			String newFlightNumberDigits = super.getRequest().getData("flightNumberDigits", String.class);
			Airport departureAirportCode = super.getRequest().getData("departureAirport", Airport.class);
			Airport arrivalAirportCode = super.getRequest().getData("arrivalAirport", Airport.class);
			Date departure = leg.getDeparture();
			Date arrival = leg.getArrival();
			Aircraft newAircraft = super.getRequest().getData("aircraft", Aircraft.class);

			if (newFlightNumberDigits != null && departureAirportCode != null && arrivalAirportCode != null && departure != null && arrival != null && newAircraft != null) {
				Optional<Leg> currentLegWithFlightNumberDigits = this.repository.findLegByFlightNumberDigits(newFlightNumberDigits);

				List<String> numbersAtTheSameTime = this.repository.findLegsByMomentBracket(departure, arrival, LegStatus.ON_TIME, LegStatus.DELAYED).stream().filter(x -> x.getId() != legId).map(x -> x.getAircraft().getRegistrationNumber()).toList();

				if (currentLegWithFlightNumberDigits.isPresent() && currentLegWithFlightNumberDigits.get().getId() != legId)
					super.state(false, "flightNumberDigits", "manager.leg.existingFlightNumberDigits");

				if (departureAirportCode.equals(arrivalAirportCode))
					super.state(false, "*", "manager.leg.sameAirports");

				if (MomentHelper.isPast(departure))
					super.state(false, "departure", "manager.leg.departureInThePast");

				if (arrival.before(departure))
					super.state(false, "arrival", "manager.leg.arrivalBeforeDeparture");

				if (numbersAtTheSameTime.contains(newAircraft.getRegistrationNumber()))
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
		//SelectChoices flightChoices;
		SelectChoices departureAirportChoices;
		SelectChoices arrivalAirportChoices;
		SelectChoices aircraftChoices;
		SelectChoices statusChoices;

		//List<Flight> flights = this.repository.findFlightsByManager(leg.getFlight().getManager().getId());
		//flightChoices = SelectChoices.from(flights, "tag", leg.getFlight());

		List<Airport> airports = this.repository.findAllAirports();
		departureAirportChoices = SelectChoices.from(airports, "iataCode", leg.getDepartureAirport());
		arrivalAirportChoices = SelectChoices.from(airports, "iataCode", leg.getArrivalAirport());

		List<Aircraft> aircrafts = this.repository.findAllAircrafts(leg.getFlight().getManager().getAirline().getIataCode());
		aircraftChoices = SelectChoices.from(aircrafts, "registrationNumber", leg.getAircraft());

		statusChoices = SelectChoices.from(LegStatus.class, leg.getStatus());

		dataset = super.unbindObject(leg, "flightNumberDigits", "departure", "arrival", "status", "draftMode");

		dataset.put("statusChoices", statusChoices);
		//dataset.put("flights", flightChoices);
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("departureAirports", departureAirportChoices);
		dataset.put("arrivalAirports", arrivalAirportChoices);

		super.getResponse().addGlobal("masterId", leg.getFlight().getId());
		super.getResponse().addData(dataset);
	}

}
