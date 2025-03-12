/*
 * Consumer.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.realms;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidEmployeeIdentifier;
import acme.constraints.ValidPhone;
import acme.constraints.ValidShortText;
import lombok.Getter;
import lombok.Setter;

@Entity

@Getter

@Setter
public class Customer extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory

	@ValidEmployeeIdentifier

	@Column(unique = true)
	private String				identifier;

	@Mandatory

	@ValidPhone

	@Automapped
	private String				phoneNumber;

	@Mandatory

	@ValidString

	@Automapped
	private String				physicalAddress;

	@Mandatory

	@ValidShortText

	@Automapped
	private String				city;

	@Mandatory

	@ValidShortText

	@Automapped
	private String				country;

	@Optional

	@ValidNumber(max = 500000)

	@Automapped
	private Integer				earnedPoints;
	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
