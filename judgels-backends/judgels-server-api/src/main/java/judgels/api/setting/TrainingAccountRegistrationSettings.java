package judgels.api.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTrainingAccountRegistrationSettings.class)
public interface TrainingAccountRegistrationSettings {
    EmailTemplate DEFAULT_ACTIVATION_EMAIL_TEMPLATE = new EmailTemplate.Builder()
            .subject("Activate your account")
            .body("#{{emailCode}}#")
            .build();

    boolean getEnabled();
    boolean getRecaptchaEnabled();
    EmailTemplate getActivationEmailTemplate();

    class Builder extends ImmutableTrainingAccountRegistrationSettings.Builder {}
}
