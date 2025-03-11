
package acme.entities.flight;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface FlightRepository extends AbstractRepository {

	@Query("SELECT MIN(l.departure) FROM Leg l WHERE l.flight.id = :flightId")
	Date findDeparture(int flightId);

	@Query("SELECT MAX(l.arrival) FROM Leg l WHERE l.flight.id = :flightId")
	Date findArrival(int flightId);

	@Query("SELECT l.departureAirport.city FROM Leg l WHERE l.flight.id = :flightId AND l.departure = (SELECT MIN(l2.departure) FROM Leg l2 WHERE l2.flight.id = :flightId)")
	String findDepartureCity(int flightId);

	@Query("SELECT l.arrivalAirport.city FROM Leg l WHERE l.flight.id = :flightId AND l.arrival = (SELECT MAX(l2.arrival) FROM Leg l2 WHERE l2.flight.id = :flightId)")
	String findArrivalCity(int flightId);

	@Query("SELECT COUNT(l) FROM Leg l WHERE l.flight.id = :flightId")
	Integer findNumberOfLegs(int flightId);

}
