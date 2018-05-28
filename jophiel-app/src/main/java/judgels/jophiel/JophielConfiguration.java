package judgels.jophiel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.fs.aws.AwsConfiguration;
import judgels.fs.aws.AwsFsConfiguration;
import judgels.jophiel.mailer.MailerConfiguration;
import judgels.jophiel.user.avatar.UserAvatarConfiguration;
import judgels.jophiel.user.password.UserResetPasswordConfiguration;
import judgels.jophiel.user.registration.UserRegistrationConfiguration;
import judgels.recaptcha.RecaptchaConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJophielConfiguration.class)
public interface JophielConfiguration {
    String getBaseDataDir();

    @JsonProperty("mailer")
    Optional<MailerConfiguration> getMailerConfig();

    @JsonProperty("recaptcha")
    Optional<RecaptchaConfiguration> getRecaptchaConfig();

    @JsonProperty("aws")
    Optional<AwsConfiguration> getAwsConfig();

    @JsonProperty("userAvatar")
    UserAvatarConfiguration getUserAvatarConfig();

    @JsonProperty("userRegistration")
    UserRegistrationConfiguration getUserRegistrationConfig();

    @JsonProperty("userResetPassword")
    UserResetPasswordConfiguration getUserResetPasswordConfig();

    @Value.Check
    default void check() {
        if (getUserAvatarConfig().getFs() instanceof AwsFsConfiguration && !getAwsConfig().isPresent()) {
            throw new IllegalStateException("aws config is required by userAvatar config");
        }

        if (getUserRegistrationConfig().getUseRecaptcha() && !getRecaptchaConfig().isPresent()) {
            throw new IllegalStateException("recaptcha config is required by userRegistration config");
        }
        if (getUserRegistrationConfig().getEnabled() && !getMailerConfig().isPresent()) {
            throw new IllegalStateException("mailer config is required by userRegistration config");
        }

        if (getUserResetPasswordConfig().getEnabled() && !getMailerConfig().isPresent()) {
            throw new IllegalStateException("mailer config is required by userResetPassword config");
        }
    }

    class Builder extends ImmutableJophielConfiguration.Builder {}
}
