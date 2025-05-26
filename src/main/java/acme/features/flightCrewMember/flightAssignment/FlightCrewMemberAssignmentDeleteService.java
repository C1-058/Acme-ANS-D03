
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flight.Leg;
import acme.entities.flightassignment.AssignmentStatus;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		FlightAssignment assignment;
		int assignmentId;
		int memberId;

		assignmentId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(assignmentId);
		memberId = assignment == null ? null : super.getRequest().getPrincipal().getActiveRealm().getId();
		status = assignment != null && assignment.getFlightCrewMember().getId() == memberId && assignment.getDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment assignment;
		int id;

		id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(id);

		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember member = this.repository.findMemberById(memberId);
		super.bindObject(assignment, "duty", "status", "remarks", "leg");
		assignment.setFlightCrewMember(member);
	}

	@Override
	public void validate(final FlightAssignment assignment) {
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		Collection<ActivityLog> logs;

		logs = this.repository.findActivityLogsByAssignmentId(assignment.getId());
		this.repository.deleteAll(logs);
		this.repository.delete(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;
		SelectChoices dutyChoice;
		SelectChoices currentStatusChoice;

		SelectChoices legChoice;
		Collection<Leg> legs;

		dutyChoice = SelectChoices.from(Duty.class, assignment.getDuty());
		currentStatusChoice = SelectChoices.from(AssignmentStatus.class, assignment.getStatus());

		legs = this.repository.findAllLegs();
		legChoice = SelectChoices.from(legs, "id", assignment.getLeg());

		dataset = super.unbindObject(assignment, "duty", "moment", "status", "remarks", "flightCrewMember", "leg", "draftMode");
		dataset.put("confirmation", false);
		dataset.put("dutyChoice", dutyChoice);
		dataset.put("currentStatusChoice", currentStatusChoice);
		dataset.put("member", assignment.getFlightCrewMember().getEmployeeCode());
		dataset.put("legChoice", legChoice);

		super.getResponse().addData(dataset);

	}
}
