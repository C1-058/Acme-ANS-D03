
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;

@Repository
public interface FlightCrewMemberActivityLogRepository extends AbstractRepository {

	@Query("SELECT al FROM ActivityLog al")
	Collection<ActivityLog> findAllActivityLogs();

	@Query("SELECT al FROM ActivityLog al where al.flightAssignment.id = ?1")
	Collection<ActivityLog> findAllActivityLogByAssignmentId(int id);

	@Query("SELECT fa FROM FlightAssignment fa where fa.id = ?1")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("SELECT al FROM ActivityLog al where al.id = ?1")
	ActivityLog findActivityLogById(int id);

	@Query("SELECT fa FROM FlightAssignment fa")
	Collection<FlightAssignment> findAllFlightAssignments();
}
