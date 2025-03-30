
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;

import acme.client.components.validation.Optional;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@ReportAsSingleViolation

@Optional
@Pattern(regexp = "^\\s*$|^\\+?\\d{6,15}$")
public @interface ValidOptionalPhone {

	// Standard validation properties -----------------------------------------

	String message() default "The phone number must follow the correct pattern";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
