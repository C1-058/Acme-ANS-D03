
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberAssignmentListPlannedService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<FlightAssignment> plannedAssignments;

		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Date currentMoment = MomentHelper.getCurrentMoment();

		plannedAssignments = this.repository.assignmentsWithPlannedLegs(currentMoment, memberId);
		super.getBuffer().addData(plannedAssignments);

	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;

		dataset = super.unbindObject(assignment, "duty", "moment", "status", "draftMode", "leg");
		super.addPayload(dataset, assignment, "remarks", "draftMode", "flightCrewMember", "leg.status");
		dataset.put("leg", assignment.getLeg().getFlightNumber());

		super.getResponse().addData(dataset);
	}
}
