
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightListService extends AbstractGuiService<Manager, Flight> {

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
		Collection<Flight> flights;
		int managerId;

		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		flights = this.repository.findAllFlightsByManager(managerId);

		super.getBuffer().addData(flights);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "requiresSelfTransfer", "cost");
		super.addPayload(dataset, flight, "description");

		dataset.put("departure", flight.getDeparture());
		dataset.put("arrival", flight.getArrival());
		dataset.put("departureCity", flight.getDepartureCity());
		dataset.put("arrivalCity", flight.getArrivalCity());
		dataset.put("numberOfLayovers", flight.getNumberOfLayovers());
		dataset.put("published", !flight.getDraftMode());
		super.getResponse().addData(dataset);
	}

}
