package judgels.jophiel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.jophiel.auth.AuthConfiguration;
import judgels.jophiel.mailer.MailerConfiguration;
import judgels.jophiel.session.SessionConfiguration;
import judgels.jophiel.user.account.UserResetPasswordConfiguration;
import judgels.jophiel.user.avatar.UserAvatarConfiguration;
import judgels.jophiel.user.superadmin.SuperadminCreatorConfiguration;
import judgels.jophiel.user.web.WebConfiguration;
import org.immutables.value.Value;
import tlx.fs.aws.AwsConfiguration;
import tlx.fs.aws.AwsFsConfiguration;
import tlx.jophiel.user.registration.UserRegistrationConfiguration;
import tlx.recaptcha.RecaptchaConfiguration;

@Value.Immutable
@JsonDeserialize(as = ImmutableJophielConfiguration.class)
public interface JophielConfiguration {
    @JsonProperty("auth")
    Optional<AuthConfiguration> getAuthConfig();

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

    @JsonProperty("superadmin")
    Optional<SuperadminCreatorConfiguration> getSuperadminCreatorConfig();

    @JsonProperty("session")
    SessionConfiguration getSessionConfig();

    @JsonProperty("web")
    WebConfiguration getWebConfig();

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
