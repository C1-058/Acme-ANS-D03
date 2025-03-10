
package acme.entities.flight;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Leg extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[0-9]{4}$")
	@Column(unique = true)
	private String				flightNumberDigits;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				departure;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				arrival;

	@Mandatory
	@Valid
	@Automapped
	private LegStatus			status;


	@Transient
	private int getDuration() {
		return this.departure.compareTo(this.arrival);
	}

	@Transient
	private String getFlightNumber() {
		return this.aircraft.getAirline().getIataCode() + this.flightNumberDigits;
	}


	@Mandatory
	@Valid
	@ManyToOne
	private Flight		flight;

	@Mandatory
	@Valid
	@ManyToOne
	private Airport		departureAirport;

	@Mandatory
	@Valid
	@ManyToOne
	private Airport		arrivalAirport;

	@Mandatory
	@Valid
	@ManyToOne
	private Aircraft	aircraft;

}
