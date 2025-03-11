
package acme.entities.aircraft;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Aircraft extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(max = 50, min = 1)
	@Automapped
	private String				model;

	@Mandatory
	@Column(unique = true)
	@ValidString(max = 50, min = 1)
	private String				registrationNumber;

	@Mandatory
	@ValidNumber(min = 1)
	@Automapped
	private Integer				capacity;

	@Mandatory
	@ValidNumber(min = 2, max = 50)
	@Automapped
	private Integer				cargoWeight;

	@Mandatory
	@Valid
	@Automapped
	private ServiceStatus		status;

	@Optional
	@ValidString
	@Automapped
	private String				details;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airline				airline;

}
