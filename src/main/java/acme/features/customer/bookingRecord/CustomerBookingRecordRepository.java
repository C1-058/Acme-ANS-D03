
package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRecord;
import acme.entities.passenger.Passenger;

@Repository
public interface CustomerBookingRecordRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select br from BookingRecord br where br.booking.id = :bookingId")
	Collection<BookingRecord> findBookingRecordsByBookingId(int bookingId);

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	//@Query("select p from Passenger p, BookingRecord br where p.id=br.passenger.id and br.id = :bookingRecordId")
	//Passenger findPassengerByBookingRecordId(int bookingRecordId);

	@Query("select br from BookingRecord br where br.id = :bookingRecordId")
	BookingRecord findBookingRecordById(int bookingRecordId);

	@Query("select p from Passenger p where p.draftMode=false and p.customer.id=:customerId and p.id not in (select br.passenger.id from BookingRecord br where br.booking.id = :bookingId)")
	Collection<Passenger> findAvailablePassengersByBookingId(int customerId, int bookingId);

	@Query("select p from Passenger p where p.draftMode=false and p.customer.id= :customerId")
	Collection<Passenger> findPassengersByCustomerId(int customerId);

	//@Query("select b from Passenger p, BookingRecord br, Booking b where p.id=br.passenger.id and br.booking.id = b.id and p.id=:id")
	//Booking findBookingByPassengerId(int id);

	//@Query("select p from Passenger p where p.id = :id")
	//Passenger findPassengerById(int id);

	//@Query("select p from Passenger p, BookingRecord br, Booking b where p.id=br.passenger.id and br.booking.id = b.id and b.customer.id=:customerId")
	//Collection<Passenger> findPassengersByCustomerId(int customerId);

	//@Query("select p from Passenger p, BookingRecord br where p.id=br.passenger.id and br.booking.id = :bookingId")
	//Collection<Passenger> findPassengersByBookingId(int bookingId);

}
