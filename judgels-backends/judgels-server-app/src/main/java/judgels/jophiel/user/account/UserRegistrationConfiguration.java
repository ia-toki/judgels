package judgels.jophiel.user.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.jophiel.EmailTemplate;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRegistrationConfiguration.class)
public interface UserRegistrationConfiguration {
    UserRegistrationConfiguration DEFAULT = new UserRegistrationConfiguration.Builder()
            .enabled(true)
            .useRecaptcha(false)
            .activationEmailTemplate(new EmailTemplate.Builder()
                    .subject("Activate your account")
                    .body("#{{emailCode}}#")
                    .build())
            .build();

    boolean getEnabled();
    boolean getUseRecaptcha();
    EmailTemplate getActivationEmailTemplate();

    class Builder extends ImmutableUserRegistrationConfiguration.Builder {}
}
