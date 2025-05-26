
package acme.constraints.validators;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.constraints.ValidTrackingLog;
import acme.entities.claim.ClaimStatus;
import acme.entities.tracking_log.TrackingLog;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		boolean result;

		assert context != null;

		if (trackingLog == null)
			super.state(context, false, "TrackingLog", "No hay trackingLogs");
		else if (trackingLog.getIndicator() != null && trackingLog.getResolution() != null && trackingLog.getClaim() != null) {

			if (trackingLog.getResolutionPercentage() != null && trackingLog.getResolutionPercentage() == 100.0)
				super.state(context, !trackingLog.getIndicator().equals(ClaimStatus.PENDING), "indicator", "El estado no puede ser PENDING");
			else
				super.state(context, trackingLog.getIndicator().equals(ClaimStatus.PENDING), "indicator", "El estado debe ser PENDING");

			if (trackingLog.getIndicator().equals(ClaimStatus.PENDING))
				super.state(context, trackingLog.getResolution() == null || trackingLog.getResolution().isBlank(), "Resolution", "El campo resolution debe quedar vacío hasta la finalización del tracking log");
			else
				super.state(context, trackingLog.getResolution() != null && !trackingLog.getResolution().isBlank(), "Resolution", "El campo resolucion es incorrecto");

		}
		result = !super.hasErrors(context);
		return result;
	}
}
