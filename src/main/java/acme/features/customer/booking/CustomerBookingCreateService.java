/*
 * CustomerPassengerCreateService.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.configuration.Configuration;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flight.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Booking booking;
		Customer customer;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		booking = new Booking();
		booking.setDraftMode(true);
		booking.setCustomer(customer);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {

		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);

		super.bindObject(booking, "locatorCode", "travelClass", "price", "lastNibble");

		booking.setFlight(flight);
		booking.setPurchaseMoment(MomentHelper.getCurrentMoment());
	}

	@Override
	public void validate(final Booking booking) {

		// Custom validation: validate locatorCode must be unique in DB
		boolean uniqueBooking;
		boolean validCurrency;

		Booking existingBooking;
		Configuration configuration;
		String acceptedCurrencies;

		existingBooking = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
		uniqueBooking = existingBooking == null || existingBooking.equals(booking);

		super.state(uniqueBooking, "locatorCode", "acme.validation.booking.duplicated-locatorCode.message");

		// Custom validation: price must be only an accepted currency
		configuration = this.repository.findConfiguration();
		acceptedCurrencies = configuration.getAcceptedCurrencies();

		Money price;
		price = booking.getPrice();

		if (price != null) {
			String currency;
			currency = price.getCurrency();

			validCurrency = StringHelper.contains(acceptedCurrencies, currency, true);

			super.state(validCurrency, "price", "acme.validation.booking.invalid-currency.message");
		}

	}

	@Override
	public void perform(final Booking booking) {
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Collection<Flight> flights;

		SelectChoices travelClassChoices, flightChoices;
		Dataset dataset;
		Date currentMoment;

		// Pendiente de hacer cuando Flights tenga draftMode, en esta rama no lo tiene aún
		currentMoment = MomentHelper.getCurrentMoment();

		// Y sólo con en draftMode=false y fecha de salida posterior a currentMoment
		flights = this.repository.findAvailablesFlights();
		//flights = this.repository.findAllFlights();

		flightChoices = SelectChoices.from(flights, "displayTag", booking.getFlight());

		travelClassChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", // 
			"customer.identity.fullName", "lastNibble", "flight", "draftMode");
		dataset.put("travelClasses", travelClassChoices);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
