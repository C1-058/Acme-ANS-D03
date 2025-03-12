
package acme.entities.purchase;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.validation.Mandatory;
import acme.entities.flight.Flight;
import acme.realms.Customer;
import lombok.Getter;
import lombok.Setter;

@Entity

@Getter

@Setter
public class Purchase {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Mandatory

	@Valid

	@ManyToOne(optional = false)

	private Flight				flight;

	@Mandatory

	@Valid

	@ManyToOne(optional = false)

	private Customer			customer;
}
