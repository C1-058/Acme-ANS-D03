
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.configuration.Configuration;
import acme.entities.booking.Booking;
import acme.entities.flight.Flight;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select b from Booking b where b.locatorCode = :locatorCode")
	Booking findBookingByLocatorCode(String locatorCode);

	@Query("select c from Customer c where c.id = :id")
	Customer findCustomerById(int id);

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findBookingsByCustomerId(int customerId);

	@Query("select b from Booking b where b.draftMode = false")
	Collection<Booking> findBookingsByAvailability();

	@Query("select p from Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("select p from Passenger p, BookingRecord br, Booking b where p.id=br.passenger.id and br.booking.id = b.id and b.customer.id=:customerId")
	Collection<Passenger> findPassengersByCustomerId(int customerId);

	@Query("select p from Passenger p, BookingRecord br where p.id=br.passenger.id and br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

	@Query("select f from Flight f")
	Collection<Flight> findAllFlights();

	//getDeparture es @Transient
	//@Query("select f from Flight f where f.draftMode=false and f.getDeparture > :currentMoment")
	//Collection<Flight> findAvailablesFlights(Date currentMoment);

	@Query("select f from Flight f where f.draftMode=false")
	Collection<Flight> findAvailablesFlights();

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("Select c from Configuration c")
	Configuration findConfiguration();

	// Number of passengers in a Booking
	@Query("select count(br.passenger.id) from BookingRecord br where br.booking.id=:bookingId")
	Integer countOfPassengersByBookingId(int bookingId);

	// Number of passengers in draftMode in a Booking
	@Query("select count(p) from Booking b, BookingRecord br, Passenger p where b.id=br.booking.id and br.passenger.id=p.id and p.draftMode=true and br.booking.id=:bookingId")
	Integer countOfPassengersInDraftModeByBookingId(int bookingId);

}
