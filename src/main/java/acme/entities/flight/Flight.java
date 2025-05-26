
package acme.entities.flight;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidShortText;
import acme.constraints.ValidText;
import acme.realms.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "manager_id")
})
public class Flight extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidShortText
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
	@ValidText
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				draftMode;


	@Transient
	public Date getDeparture() {
		Date res;
		FlightRepository repository;

		repository = SpringHelper.getBean(FlightRepository.class);
		res = repository.findDeparture(this.getId()).orElse(null);

		return res;
	}

	@Transient
	public Date getArrival() {
		Date res;
		FlightRepository repository;

		repository = SpringHelper.getBean(FlightRepository.class);
		res = repository.findArrival(this.getId()).orElse(null);

		return res;
	}

	@Transient
	public String getDepartureCity() {
		String res;
		FlightRepository repository;

		repository = SpringHelper.getBean(FlightRepository.class);
		res = repository.findDepartureCity(this.getId()).orElse(null);

		return res;
	}

	@Transient
	public String getArrivalCity() {
		String res;
		FlightRepository repository;

		repository = SpringHelper.getBean(FlightRepository.class);
		res = repository.findArrivalCity(this.getId()).orElse(null);

		return res;
	}

	@Transient
	public Integer getNumberOfLayovers() {
		Integer res;
		FlightRepository repository;

		repository = SpringHelper.getBean(FlightRepository.class);
		res = repository.findNumberOfLegs(this.getId());

		return res == 0 ? res : res - 1;
	}

	@Transient
	public String getDisplayTag() {

		Date scheduledDeparture;
		Date scheduledArrival;

		scheduledDeparture = this.getDeparture();
		scheduledArrival = this.getArrival();

		int departureHour = scheduledDeparture != null ? this.getDeparture().getHours() : 0;
		int departureMinute = scheduledDeparture != null ? this.getDeparture().getMinutes() : 0;
		int arrivalHour = scheduledArrival != null ? this.getArrival().getHours() : 0;
		int arrivalMinute = scheduledArrival != null ? this.getArrival().getMinutes() : 0;

		return String.format("%02d:%02d %s → %02d:%02d %s", departureHour, departureMinute, this.getDepartureCity(), arrivalHour, arrivalMinute, this.getArrivalCity());
	}


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Manager manager;

}
