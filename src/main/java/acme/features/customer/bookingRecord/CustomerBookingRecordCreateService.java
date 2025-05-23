
package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRecord;
import acme.entities.booking.TravelClass;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordCreateService extends AbstractGuiService<Customer, BookingRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		int customerId;
		Booking booking;

		bookingId = super.getRequest().getData("masterId", int.class);
		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		// Ensure that the booking to add the passenger exists and belongs to principal
		booking = this.repository.findBookingById(bookingId);

		status = booking != null && customerId == booking.getCustomer().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		BookingRecord bookingRecord;
		Booking booking;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);

		bookingRecord = new BookingRecord();
		bookingRecord.setPassenger(null);
		bookingRecord.setBooking(booking);

		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void bind(final BookingRecord bookingRecord) {
		int passengerId;
		int masterId;
		Booking booking;
		Passenger passenger;

		passengerId = super.getRequest().getData("passenger", int.class);
		passenger = this.repository.findPassengerById(passengerId);

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);

		super.bindObject(bookingRecord, "passenger");
		bookingRecord.setBooking(booking);
	}

	@Override
	public void validate(final BookingRecord bookingRecord) {

		// Custom validation 1: validate selected passenger Id belongs to principal
		boolean validPassenger;

		int masterId;
		int customerId;

		Collection<Passenger> passengers;
		Passenger selectedPassenger;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		masterId = super.getRequest().getData("masterId", int.class);

		passengers = this.repository.findAvailablePassengersByBookingId(customerId, masterId);

		selectedPassenger = bookingRecord.getPassenger();

		validPassenger = passengers.contains(selectedPassenger);

		super.state(validPassenger, "*", "acme.validation.booking.invalid-passenger.message");

	}

	@Override
	public void perform(final BookingRecord bookingRecord) {
		this.repository.save(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		int masterId;
		int customerId;

		Collection<Passenger> passengers;
		SelectChoices passengerChoices;
		SelectChoices travelClassChoices;
		Passenger selectedPassenger;
		Dataset dataset;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		masterId = super.getRequest().getData("masterId", int.class);

		passengers = this.repository.findAvailablePassengersByBookingId(customerId, masterId);
		selectedPassenger = bookingRecord.getPassenger();

		passengerChoices = SelectChoices.from(passengers, "fullName", !passengers.contains(selectedPassenger) ? null : selectedPassenger);

		travelClassChoices = SelectChoices.from(TravelClass.class, bookingRecord.getBooking().getTravelClass());

		dataset = super.unbindObject(bookingRecord, "booking.locatorCode", "booking.customer.identity.fullName", //
			"booking.travelClass", "booking.price");
		dataset.put("passenger", passengerChoices.getSelected().getKey());
		dataset.put("passengers", passengerChoices);
		dataset.put("travelClasses", travelClassChoices);

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addData(dataset);
	}

}
