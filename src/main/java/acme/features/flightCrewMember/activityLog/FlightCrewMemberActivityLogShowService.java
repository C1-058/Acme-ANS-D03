
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		ActivityLog log;
		int logId;
		int memberId;
		boolean status;

		logId = super.getRequest().getData("id", int.class);
		log = this.repository.findActivityLogById(logId);
		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		status = log != null && log.getFlightAssignment().getFlightCrewMember().getId() == memberId;
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		ActivityLog log;
		int id;

		id = super.getRequest().getData("id", int.class);
		log = this.repository.findActivityLogById(id);
		super.getBuffer().addData(log);
	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset dataset;

		SelectChoices assignmentChoices;
		Collection<FlightAssignment> assignments;

		assignments = this.repository.findAllFlightAssignments();
		assignmentChoices = SelectChoices.from(assignments, "id", log.getFlightAssignment());

		dataset = super.unbindObject(log, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draftMode", "flightAssignment");
		dataset.put("assignmentChoices", assignmentChoices);

		super.getResponse().addData(dataset);
	}

}
