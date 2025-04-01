
package acme.features.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightShowService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("id", int.class);

		flight = this.repository.findFlightById(flightId);

		if (flight != null) {
			int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			status = flight.getManager().getId() == managerId;
		} else
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight flight;
		int id;

		id = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(id);

		super.getBuffer().addData(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "requiresSelfTransfer", "cost", "description", "draftMode");

		dataset.put("departure", flight.getDeparture());
		dataset.put("arrival", flight.getArrival());
		dataset.put("departureCity", flight.getDepartureCity());
		dataset.put("arrivalCity", flight.getArrivalCity());
		dataset.put("numberOfLayovers", flight.getNumberOfLayovers());
		dataset.put("airline", flight.getManager().getAirline().getIataCode());

		super.getResponse().addData(dataset);
	}
}
