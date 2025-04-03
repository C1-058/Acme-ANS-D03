
package acme.features.assistanceAgent.trackingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimStatus;
import acme.entities.tracking_log.TrackingLog;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogShowService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int trackingLogId = super.getRequest().getData("id", int.class);
		TrackingLog trackingLog = this.repository.findTrackingLogById(trackingLogId);

		super.getResponse().setAuthorised(agentId == trackingLog.getClaim().getAssistanceAgent().getId());
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);
		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {

		Collection<Claim> claims;
		SelectChoices statusChoices;
		SelectChoices claimChoices;
		Dataset dataset;
		int assistanceAgentId;
		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		statusChoices = SelectChoices.from(ClaimStatus.class, trackingLog.getIndicator());

		claims = this.repository.findClaimsByAssistanceAgent(assistanceAgentId);
		claimChoices = SelectChoices.from(claims, "id", trackingLog.getClaim());

		dataset = super.unbindObject(trackingLog, "claim", "lastUpdateMoment", "step", "resolutionPercentage", "indicator", "resolution", "draftMode", "id");
		dataset.put("statusChoices", statusChoices);
		dataset.put("claimChoices", claimChoices);

		super.getResponse().addData(dataset);

	}

}
