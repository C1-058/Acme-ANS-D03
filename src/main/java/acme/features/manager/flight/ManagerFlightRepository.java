
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flight.Flight;
import acme.entities.flight.Leg;
import acme.realms.Manager;

@Repository
public interface ManagerFlightRepository extends AbstractRepository {

	@Query("select m from Manager m where m.id = :managerId")
	Manager findManagerById(int managerId);

	@Query("select f from Flight f")
	Collection<Flight> findAllFlights();

	@Query("select f from Flight f where f.manager.id = :managerId")
	Collection<Flight> findAllFlightsByManager(int managerId);

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("select l from Leg l where l.flight.id = :flightId")
	Collection<Leg> findLegsByFlightId(int flightId);

}
