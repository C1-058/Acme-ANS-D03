
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

@Pattern(regexp = "^[A-Z0-9]{6,8}$")
public @interface ValidLocatorCode {
	// Standard validation properties -----------------------------------------

	String message() default "The code must follow the correct locator code pattern.";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
