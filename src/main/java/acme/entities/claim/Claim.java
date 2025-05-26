
package acme.entities.claim;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidClaim;
import acme.constraints.ValidText;
import acme.entities.flight.Leg;
import acme.entities.tracking_log.TrackingLog;
import acme.features.assistanceAgent.trackingLog.AssistanceAgentTrackingLogRepository;
import acme.realms.AssistanceAgent;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidClaim
@Table(indexes = {
	@Index(columnList = "assistance_agent_id"), @Index(columnList = "id")
})
public class Claim extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				passengerEmail;

	@Mandatory
	@ValidText
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@Automapped
	private ClaimType			type;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private AssistanceAgent		assistanceAgent;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Leg					leg;


	@Transient
	public ClaimStatus getIndicator() {
		AssistanceAgentTrackingLogRepository repository;

		repository = SpringHelper.getBean(AssistanceAgentTrackingLogRepository.class);
		return repository.findOrderTrackingLog(this.getId()).flatMap(list -> list.stream().findFirst()).map(TrackingLog::getIndicator).orElse(ClaimStatus.PENDING);
	}

}
