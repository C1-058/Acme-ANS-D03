
package acme.entities.flight;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				tag;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				requiresSelfTransfer;

	@Mandatory
	@ValidMoney
	@Automapped
	private Money				cost;

	@Optional
	@ValidString
	@Automapped
	private String				description;


	@Transient
	public Date getDeparture() {
		Date res;
		FlightRepository repository;

		repository = SpringHelper.getBean(FlightRepository.class);
		res = repository.findDeparture(this.getId());

		return res;
	}

	@Transient
	public Date getArrival() {
		Date res;
		FlightRepository repository;

		repository = SpringHelper.getBean(FlightRepository.class);
		res = repository.findArrival(this.getId());

		return res;
	}

	@Transient
	public String getDepartureCity() {
		String res;
		FlightRepository repository;

		repository = SpringHelper.getBean(FlightRepository.class);
		res = repository.findDepartureCity(this.getId());

		return res;
	}

	@Transient
	public String getArrivalCity() {
		String res;
		FlightRepository repository;

		repository = SpringHelper.getBean(FlightRepository.class);
		res = repository.findArrivalCity(this.getId());

		return res;
	}

	@Transient
	public Integer getNumberOfLayovers() {
		Integer res;
		FlightRepository repository;

		repository = SpringHelper.getBean(FlightRepository.class);
		res = repository.findNumberOfLegs(this.getId()) - 1;

		return res;
	}

}
