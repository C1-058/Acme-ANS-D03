
package acme.features.authenticated.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.LegStatus;
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
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Collection<FlightAssignment> assignments;
		Collection<LegStatus> statuses = List.of(LegStatus.DELAYED, LegStatus.ON_TIME);

		assignments = this.repository.assignmentsWithPlannedLegs(statuses, memberId);
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
