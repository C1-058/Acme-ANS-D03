
package acme.features.manager.leg;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.entities.flight.Leg;
import acme.entities.flight.LegStatus;

@Repository
public interface ManagerLegRepository extends AbstractRepository {

	@Query("select l from Leg l where l.id = :legId")
	Leg findLegById(int legId);

	@Query("select l from Leg l where l.flight.id = :masterId")
	Collection<Leg> findLegsByFlight(int masterId);

	@Query("select f from Flight f where f.id = :masterId")
	Flight findFlightById(int masterId);

	@Query("select f from Flight f where f.manager.id = :managerId")
	List<Flight> findFlightsByManager(int managerId);

	@Query("select a from Airport a")
	List<Airport> findAllAirports();

	@Query("select a from Aircraft a")
	List<Aircraft> findAllAircrafts();

	@Query("select l from Leg l where l.flightNumberDigits = :flightNumberDigits")
	Optional<Leg> findLegByFlightNumberDigits(String flightNumberDigits);

	@Query("select l from Leg l where l.departure < :arrival and l.arrival > :departure and (l.status = :onTime or l.status = :delayed)")
	List<Leg> findLegsByMomentBracket(Date departure, Date arrival, LegStatus onTime, LegStatus delayed);
}
