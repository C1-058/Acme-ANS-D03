
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int masterId;
		int memberId;
		FlightAssignment assignment;
		boolean status;

		masterId = super.getRequest().getData("masterId", int.class);
		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		assignment = this.repository.findFlightAssignmentById(masterId);

		status = assignment.getFlightCrewMember().getId() == memberId;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<ActivityLog> logs;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		logs = this.repository.findAllActivityLogByAssignmentId(masterId);
		super.getBuffer().addData(logs);
	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset dataset;
		int masterId;

		dataset = super.unbindObject(log, "registrationMoment", "typeOfIncident", "securityLevel");
		masterId = super.getRequest().getData("masterId", int.class);

		super.addPayload(dataset, log, "draftMode");
		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addData(dataset);

	}
}
