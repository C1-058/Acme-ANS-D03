
package acme.constraints.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import acme.constraints.ValidText;

public class NotBlankOrNullValidator implements ConstraintValidator<ValidText, String> {

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		return value == null || !value.trim().isEmpty();
	}
}
