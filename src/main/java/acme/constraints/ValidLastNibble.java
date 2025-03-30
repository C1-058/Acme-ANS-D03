
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@ReportAsSingleViolation

@Pattern(regexp = "^[0-9]{4}$")
public @interface ValidLastNibble {

	// Standard validation properties -----------------------------------------

	String message() default "The code must follow the correct last nibble pattern";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
