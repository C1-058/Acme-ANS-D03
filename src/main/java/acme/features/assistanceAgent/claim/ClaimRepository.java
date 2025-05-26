
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.flight.Leg;
import acme.entities.tracking_log.TrackingLog;

@Repository
public interface ClaimRepository extends AbstractRepository {

	@Query("Select c from Claim c where c.assistanceAgent.id=:agentId")
	List<Claim> findClaimsByAssistanceAgent(int agentId);

	@Query("Select c from Claim c where c.id=:claimId")
	Claim findClaimById(int claimId);

	@Query("SELECT l FROM Leg l WHERE l.id = :legId")
	Leg findLegByLegId(int legId);

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false")
	Collection<Leg> findAllLegPublish();

	@Query("select tl FROM TrackingLog tl where tl.claim.id = :claimId")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);

	@Query("select l FROM Leg l where l.id = :legId")
	Leg getLegById(int legId);

}
