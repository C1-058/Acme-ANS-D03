
package acme.constraints.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import acme.constraints.ValidShortText;

public class NotBlankOrNullValidatorShortText implements ConstraintValidator<ValidShortText, String> {

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		return value == null || !value.trim().isEmpty();
	}
}
