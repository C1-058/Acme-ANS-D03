
package acme.features.flightCrewMember.flightAssignment;

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
	private FlightCrewMemberAssignmentShowService			showService;

	@Autowired
	private FlightCrewMemberAssignmentCreateService			createService;

	@Autowired
	private FlightCrewMemberAssignmentUpdateService			updateService;

	@Autowired
	private FlightCrewMemberAssignmentDeleteService			deleteService;

	@Autowired
	private FlightCrewMemberAssignmentPublishService		publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listCompletedService);
		super.addCustomCommand("list-planned", "list", this.listPlannedService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("publish", "update", this.publishService);

	}
}
