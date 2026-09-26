package judgels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.nio.file.Path;
import java.util.Optional;
import judgels.app.JudgelsAppConfiguration;
import judgels.auth.AuthConfiguration;
import judgels.grading.JudgelsServerGradingConfiguration;
import judgels.mailer.MailerConfiguration;
import judgels.messaging.rabbitmq.RabbitMQConfiguration;
import judgels.recaptcha.RecaptchaConfiguration;
import judgels.user.account.UserResetPasswordConfiguration;
import judgels.user.registration.UserRegistrationConfiguration;
import judgels.user.superadmin.SuperadminCreatorConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJudgelsServerConfiguration.class)
public interface JudgelsServerConfiguration {
    Path getBaseDataDir();

    @JsonProperty("app")
    JudgelsAppConfiguration getAppConfig();

    @JsonProperty("rabbitmq")
    Optional<RabbitMQConfiguration> getRabbitMQConfig();

    @JsonProperty("grading")
    JudgelsServerGradingConfiguration getGradingConfig();

    @JsonProperty("auth")
    Optional<AuthConfiguration> getAuthConfig();

    @JsonProperty("mailer")
    Optional<MailerConfiguration> getMailerConfig();

    @JsonProperty("userResetPassword")
    UserResetPasswordConfiguration getUserResetPasswordConfig();

    @JsonProperty("superadmin")
    Optional<SuperadminCreatorConfiguration> getSuperadminCreatorConfig();

    @JsonProperty("recaptcha")
    Optional<RecaptchaConfiguration> getRecaptchaConfig();

    @JsonProperty("userRegistration")
    Optional<UserRegistrationConfiguration> getUserRegistrationConfig();

    @Value.Check
    default void check() {
        if (getUserRegistrationConfig().isPresent() && getUserRegistrationConfig().get().getUseRecaptcha() && !getRecaptchaConfig().isPresent()) {
            throw new IllegalStateException("recaptcha config is required by userRegistration config");
        }
        if (getUserRegistrationConfig().isPresent() && getUserRegistrationConfig().get().getEnabled() && !getMailerConfig().isPresent()) {
            throw new IllegalStateException("mailer config is required by userRegistration config");
        }

        if (getUserResetPasswordConfig().getEnabled() && !getMailerConfig().isPresent()) {
            throw new IllegalStateException("mailer config is required by userResetPassword config");
        }
    }

    class Builder extends ImmutableJudgelsServerConfiguration.Builder {}
}
