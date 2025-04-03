
package acme.realms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidEmployeeIdentifier;
import acme.constraints.ValidText;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AssistanceAgent extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidEmployeeIdentifier
	@Column(unique = true)
	private String				employeeCode;

	@Mandatory
	@ValidText
	@Automapped
	private String				spokenLanguagues;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				moment;

	@Optional
	@ValidText
	@Automapped
	private String				bio;

	@Optional
	@ValidMoney
	@Automapped
	private Money				salary;

	@Optional
	@ValidUrl
	@Automapped
	private String				pictureUrl;

	@Optional
	@Valid
	@ManyToOne(optional = true)
	private Airline				airline;

}
