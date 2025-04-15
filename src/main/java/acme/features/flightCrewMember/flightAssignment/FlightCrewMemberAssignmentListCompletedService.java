
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.LegStatus;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberAssignmentListCompletedService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

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
		LegStatus status = LegStatus.LANDED;
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Collection<FlightAssignment> assignments;

		assignments = this.repository.assignmentsWithCompletedLegs(status, memberId);
		super.getBuffer().addData(assignments);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;

		dataset = super.unbindObject(assignment, "duty", "moment", "status");
		super.addPayload(dataset, assignment, "remarks", "draftMode", "flightCrewMember", "leg.status");

		super.getResponse().addData(dataset);
	}
}
