
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	//@Query("select b from Booking b where b.id = :id")
	//Booking findBookingById(int id);

	@Query("select c from Customer c where c.id = :id")
	Customer findCustomerById(int id);

	//@Query("select b from Passenger p, BookingRecord br, Booking b where p.id=br.passenger.id and br.booking.id = b.id and p.id=:id")
	//Booking findBookingByPassengerId(int id);

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	@Query("select p from Passenger p where p.passportNumber = :passportNumber")
	Passenger findPassengerByPassportNumber(String passportNumber);

	@Query("select p from Passenger p where p.customer.id = :id")
	Collection<Passenger> findAllPassengersByCustomerId(int id);

	@Query("select p from Passenger p, BookingRecord br, Booking b where p.id=br.passenger.id and br.booking.id = b.id and b.customer.id=:customerId")
	Collection<Passenger> findPassengersByCustomerId(int customerId);

	@Query("select p from Passenger p, BookingRecord br where p.id=br.passenger.id and br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

}
