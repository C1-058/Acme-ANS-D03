/*
 * CustomerBookingRecordShowService.java
 *
 * Copyright (C) 2025 Rafael David Caro Medina
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.BookingRecord;
import acme.entities.booking.TravelClass;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordShowService extends AbstractGuiService<Customer, BookingRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id, customerId;

		BookingRecord bookingRecord;

		id = super.getRequest().getData("id", int.class);
		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		bookingRecord = this.repository.findBookingRecordById(id);

		status = bookingRecord != null && super.getRequest().getPrincipal().hasRealm(bookingRecord.getPassenger().getCustomer()) //
			&& customerId == bookingRecord.getPassenger().getCustomer().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		BookingRecord bookingRecord;

		int id;

		id = super.getRequest().getData("id", int.class);
		bookingRecord = this.repository.findBookingRecordById(id);

		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		Collection<Passenger> passengers;
		SelectChoices passengerChoices, travelClassChoices;
		Passenger selectedPassenger;
		Dataset dataset;
		int customerId;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		passengers = this.repository.findPassengersByCustomerId(customerId);
		selectedPassenger = bookingRecord.getPassenger();

		passengerChoices = SelectChoices.from(passengers, "fullName", !passengers.contains(selectedPassenger) ? null : selectedPassenger);

		travelClassChoices = SelectChoices.from(TravelClass.class, bookingRecord.getBooking().getTravelClass());

		dataset = super.unbindObject(bookingRecord, "booking.locatorCode", "booking.customer.identity.fullName", //
			"booking.travelClass", "booking.price");
		dataset.put("passenger", bookingRecord.getPassenger().getId());
		dataset.put("passengers", passengerChoices);
		dataset.put("travelClasses", travelClassChoices);

		super.getResponse().addData(dataset);
	}

}
