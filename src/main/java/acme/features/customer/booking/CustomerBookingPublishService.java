/*
 * CustomerPassengerUpdateService.java
 *
 * Copyright 2025 Rafael David Caro Medina
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

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.StringHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.configuration.Configuration;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flight.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractService<Customer, Booking> -------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		int customerId;

		Booking booking;
		Customer customer;

		masterId = super.getRequest().getData("id", int.class);
		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		booking = this.repository.findBookingById(masterId);
		customer = booking == null ? null : booking.getCustomer();

		// && booking.isDraftMode() ??
		status = booking != null && customer != null && super.getRequest().getPrincipal().hasRealm(customer) && //
			customerId == customer.getId();

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
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);

		super.bindObject(booking, "locatorCode", "travelClass", "price", "lastNibble");

		booking.setFlight(flight);
	}

	@Override
	public void validate(final Booking booking) {

		// Custom validation 1: validate locatorCode must be unique in DB
		boolean uniqueBooking;
		boolean validCurrency;

		Booking existingBooking;
		Configuration configuration;
		String acceptedCurrencies;

		existingBooking = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
		uniqueBooking = existingBooking == null || existingBooking.equals(booking);

		super.state(uniqueBooking, "locatorCode", "acme.validation.booking.duplicated-locatorCode.message");

		// Custom validation 2: A booking can be published only when the last credit card nibble has been stored
		boolean cardNibbleStored;

		cardNibbleStored = booking != null && booking.getLastNibble() != null && !booking.getLastNibble().isBlank() && booking.getLastNibble().length() >= 4;

		super.state(cardNibbleStored, "lastNibble", "acme.validation.booking.lastNibble.message");

		// Custom validation 3: price must be only an accepted currency
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

		// Custom validation 4: A booking can be published ONLY when it has AT LEAST one passenger associated
		boolean hasPassengers;
		int numPassengers;

		numPassengers = this.repository.countOfPassengersByBookingId(booking.getId());

		hasPassengers = numPassengers > 0;

		super.state(hasPassengers, "*", "acme.validation.booking.passengersNumber.message");

		// Custom validation 5: A booking can be published ONLY when ALL of its passengers are not in draftMode
		boolean allPassengersPublished;
		int numPassengersInDraftMode;

		numPassengersInDraftMode = this.repository.countOfPassengersInDraftModeByBookingId(booking.getId());

		allPassengersPublished = numPassengersInDraftMode == 0;

		super.state(allPassengersPublished, "*", "acme.validation.booking.passengersInDraftMode.message");
	}

	@Override
	public void perform(final Booking booking) {
		booking.setDraftMode(false);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Collection<Flight> flights;

		SelectChoices travelClassChoices, flightChoices;
		Dataset dataset;

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
