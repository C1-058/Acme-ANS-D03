
package acme.realms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Manager extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@Mandatory
	@Column(unique = true)
	private String				identifierNumber;

	@Valid
	@Mandatory
	private Integer				yearsOfExperience;

	@ValidMoment
	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	private Date				birthdate;

	@ValidUrl
	@Optional
	private String				pictureUrl;

}
