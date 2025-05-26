
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Leg;
import acme.entities.flightassignment.AssignmentStatus;
import acme.entities.flightassignment.Duty;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		if (super.getRequest().getMethod().equals("GET"))
			status = true;
		else {
			int legId = super.getRequest().getData("leg", int.class);
			Leg leg = this.repository.findLegById(legId);
			boolean legStatus = legId == 0 || leg != null;
			status = legStatus; // && super.getRequest().getPrincipal().hasRealm(assignment.getFlightCrewMember());
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;
		FlightCrewMember flightCrewMember = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		flightAssignment = new FlightAssignment();
		flightAssignment.setFlightCrewMember(flightCrewMember);
		flightAssignment.setMoment(MomentHelper.getCurrentMoment());
		flightAssignment.setDraftMode(true);
		super.getBuffer().addData(flightAssignment);

	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		super.bindObject(flightAssignment, "duty", "status", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		Leg leg = flightAssignment.getLeg();

		if (leg != null) {
			boolean isPublished = !leg.getDraftMode();
			super.state(!isPublished, "leg", "acme.validation.flight-crew-member.assignment.form.error.leg-not-published", flightAssignment);
		}
	}

	@Override
	public void perform(final FlightAssignment assignment) {
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
