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

package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flight.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId, customerId;
		Booking booking;
		Customer customer;

		masterId = super.getRequest().getData("id", int.class);
		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		booking = this.repository.findBookingById(masterId);
		customer = booking == null ? null : booking.getCustomer();

		status = customer != null && super.getRequest().getPrincipal().hasRealm(customer) && customerId == customer.getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Booking booking;
		int id;

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);

		super.getBuffer().addData(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		int customerId;
		Collection<Flight> flights;
		SelectChoices travelClassChoices, flightChoices;
		Dataset dataset;

		// Y sólo con en draftMode=false y fecha de salida posterior a currentMoment
		//flights = this.repository.findAvailablesFlights();
		flights = this.repository.findAllFlights();

		flightChoices = SelectChoices.from(flights, "displayTag", booking.getFlight());

		travelClassChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", //
			"price", "customer.identity.fullName", "lastNibble", "flight", "draftMode");
		dataset.put("travelClasses", travelClassChoices);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
