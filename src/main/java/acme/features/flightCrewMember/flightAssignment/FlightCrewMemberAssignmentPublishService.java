
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Leg;
import acme.entities.flightassignment.AssignmentStatus;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.AvailabilityStatus;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int assignmentId;
		FlightAssignment assignment;
		int memberId;

		assignmentId = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(assignmentId);
		memberId = assignment == null ? null : super.getRequest().getPrincipal().getActiveRealm().getId();
		status = assignment.getFlightCrewMember().getId() == memberId && assignment.getDraftMode() && assignment != null;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int assignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(assignmentId);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		super.bindObject(assignment, "duty", "status", "remarks", "leg");

		int id = super.getRequest().getData("id", int.class);
		FlightAssignment original = this.repository.findFlightAssignmentById(id);
		assignment.setFlightCrewMember(original.getFlightCrewMember());
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		AssignmentStatus status = assignment.getStatus();
		boolean canBePublished = status == AssignmentStatus.CANCELLED || status == AssignmentStatus.CONFIRMED;
		super.state(canBePublished, "assignmentStatus", "acme.validation.flight-assignment-status-not-published");

		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean availableMember = this.repository.findMemberById(memberId).getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);
		super.state(availableMember, "flightCrewMember", "acme.validation.flight-assignment.flight-crew-member.available");

	}

	@Override
	public void perform(final FlightAssignment assignment) {
		assignment.setDraftMode(false);
		this.repository.save(assignment);
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
