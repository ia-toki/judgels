package judgels.api.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTrainingAccountPasswordResetSettings.class)
public interface TrainingAccountPasswordResetSettings {
    EmailTemplate DEFAULT_REQUEST_EMAIL_TEMPLATE = new EmailTemplate.Builder()
            .subject("Someone requested to reset your password")
            .body("#{{emailCode}}#")
            .build();

    EmailTemplate DEFAULT_RESET_EMAIL_TEMPLATE = new EmailTemplate.Builder()
            .subject("Your password has been reset")
            .body("Your password has been reset")
            .build();

    boolean getEnabled();
    EmailTemplate getRequestEmailTemplate();
    EmailTemplate getResetEmailTemplate();

    class Builder extends ImmutableTrainingAccountPasswordResetSettings.Builder {}
}
