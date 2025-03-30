
package acme.entities.flight;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface FlightRepository extends AbstractRepository {

	@Query("SELECT MIN(l.departure) FROM Leg l WHERE l.flight.id = :flightId")
	Optional<Date> findDeparture(int flightId);

	@Query("SELECT MAX(l.arrival) FROM Leg l WHERE l.flight.id = :flightId")
	Optional<Date> findArrival(int flightId);

	@Query(value = "SELECT a.city FROM leg l JOIN airport a ON l.departure_airport_id = a.id WHERE l.flight_id = :flightId ORDER BY l.departure ASC LIMIT 1", nativeQuery = true)
	Optional<String> findDepartureCity(int flightId);

	@Query(value = "SELECT a.city FROM leg l JOIN airport a ON l.arrival_airport_id = a.id WHERE l.flight_id = :flightId ORDER BY l.arrival DESC LIMIT 1", nativeQuery = true)
	Optional<String> findArrivalCity(int flightId);

	@Query("SELECT COUNT(l) FROM Leg l WHERE l.flight.id = :flightId")
	Integer findNumberOfLegs(int flightId);

}
