
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flight.Leg;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmembers.FlightCrewMember;

@Repository
public interface FlightCrewMemberAssignmentRepository extends AbstractRepository {

	@Query("select f from FlightAssignment f where f.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select f from FlightAssignment f where f.leg.arrival < :currentMoment and f.flightCrewMember.id = :memberId")
	Collection<FlightAssignment> assignmentsWithCompletedLegs(Date currentMoment, Integer memberId);

	@Query("select f from FlightAssignment f where f.leg.arrival > :currentMoment and f.flightCrewMember.id = :memberId")
	Collection<FlightAssignment> assignmentsWithPlannedLegs(Date currentMoment, Integer memberId);

	@Query("SELECT l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(Integer id);

	@Query("SELECT fcm FROM FlightCrewMember fcm")
	Collection<FlightCrewMember> findAllFlightCrewMembers();

	@Query("select al from ActivityLog al where al.flightAssignment.id = ?1")
	Collection<ActivityLog> findActivityLogsByAssignmentId(int id);

	@Query("select case when count(a) > 0 then true else false end " + "from FlightAssignment a " + "where a.id = :id " + "and a.leg.arrival < :currentMoment")
	Boolean areLegsCompleted(int id, Date currentMoment);

	@Query("select distinct a.leg from FlightAssignment a where a.flightCrewMember.id = :id")
	Collection<Leg> findLegsByCrewId(int id);

	@Query("select m from FlightCrewMember m where m.id = :id")
	FlightCrewMember findMemberById(int id);
}
