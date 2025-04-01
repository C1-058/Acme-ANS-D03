
package acme.features.authenticated.flightCrewMember.flightAssignment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@GuiController
public class FlightCrewMemberAssignmentController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberAssignmentListCompletedService	listCompletedService;

	@Autowired
	private FlightCrewMemberAssignmentListPlannedService	listPlannedService;

	@Autowired
	private FlightCrewMemberFlightAssignmentShowService		showService;

	@Autowired
	private FlightCrewMemberFlightAssignmentCreateService	createService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listCompletedService);
		super.addCustomCommand("list-planned", "list", this.listPlannedService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
	}
}
