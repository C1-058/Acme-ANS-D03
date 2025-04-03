
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.flight.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerFlightPublishService extends AbstractGuiService<Manager, Flight> {

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
			status = flight.getManager().getId() == managerId && flight.getDraftMode();
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
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "cost", "requiresSelfTransfer", "description");
	}

	@Override
	public void validate(final Flight flight) {
		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		super.state(!legs.isEmpty(), "*", "manager.flight.form.publish.noLegs");

		for (Leg leg : legs) {
			boolean isPublished = !leg.getDraftMode();
			super.state(isPublished, "*", "manager.flight.form.publish.legNotPublished");
		}
	}

	@Override
	public void perform(final Flight flight) {
		flight.setDraftMode(false);
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;
		dataset = super.unbindObject(flight, "tag", "cost", "requiresSelfTransfer", "description", "draftMode");
		super.getResponse().addData(dataset);
	}

}
