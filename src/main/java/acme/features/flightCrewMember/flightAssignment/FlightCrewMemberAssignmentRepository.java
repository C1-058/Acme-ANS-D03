
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flight.Leg;
import acme.entities.flight.LegStatus;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@Repository
public interface FlightCrewMemberAssignmentRepository extends AbstractRepository {

	@Query("select f from FlightAssignment f where f.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select f from FlightAssignment f where f.leg.status = :status and f.flightCrewMember.id = :memberId")
	Collection<FlightAssignment> assignmentsWithCompletedLegs(LegStatus status, Integer memberId);

	@Query("select f from FlightAssignment f where f.leg.status in ?1 and f.flightCrewMember.id = ?2")
	Collection<FlightAssignment> assignmentsWithPlannedLegs(Collection<LegStatus> statuses, Integer member);

	@Query("SELECT l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(Integer id);

	@Query("SELECT fcm FROM FlightCrewMember fcm")
	Collection<FlightCrewMember> findAllFlightCrewMembers();

	@Query("select al from ActivityLog al where al.flightAssignment.id = ?1")
	Collection<ActivityLog> findActivityLogsByAssignmentId(int id);
}
