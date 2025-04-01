
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.flight.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerLegListService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int managerId;
		int masterId;
		Flight flight;

		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		masterId = super.getRequest().getData("masterId", int.class);

		flight = this.repository.findFlightById(masterId);

		status = super.getRequest().getPrincipal().hasRealmOfType(Manager.class) && flight.getManager().getId() == managerId;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Leg> legs;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);

		legs = this.repository.findLegsByFlight(masterId);

		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "departure", "arrival", "status");

		dataset.put("duration", leg.getDuration());
		dataset.put("flightNumber", leg.getFlightNumber());
		dataset.put("published", !leg.getDraftMode());

		super.getResponse().addData(dataset);
	}

}
