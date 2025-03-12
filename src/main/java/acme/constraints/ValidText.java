
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Size;

import acme.constraints.validators.NotBlankOrNullValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {
	NotBlankOrNullValidator.class
})
@ReportAsSingleViolation

@Size(min = 1, max = 255)
public @interface ValidText {

	// Standard validation properties -----------------------------------------

	String message() default "The text must not consist of empty characters and must have a length between 1 and 255 characters.";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
