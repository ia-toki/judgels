package judgels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.nio.file.Path;
import java.util.Optional;
import judgels.core.auth.AuthConfiguration;
import judgels.core.mailer.MailerConfiguration;
import judgels.core.messaging.RabbitMQConfiguration;
import judgels.core.recaptcha.RecaptchaConfiguration;
import judgels.grading.JudgelsServerGradingConfiguration;
import judgels.training.TrainingConfiguration;
import judgels.user.account.UserResetPasswordConfiguration;
import judgels.user.rating.UserRatingConfiguration;
import judgels.user.registration.UserRegistrationConfiguration;
import judgels.user.superadmin.SuperadminCreatorConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJudgelsServerConfiguration.class)
public interface JudgelsServerConfiguration {
    Path getBaseDataDir();

    @JsonProperty("rabbitmq")
    Optional<RabbitMQConfiguration> getRabbitMQConfig();

    @JsonProperty("grading")
    JudgelsServerGradingConfiguration getGradingConfig();

    @JsonProperty("auth")
    Optional<AuthConfiguration> getAuthConfig();

    @JsonProperty("mailer")
    Optional<MailerConfiguration> getMailerConfig();

    // Deprecated: Only read to seed the user reset password settings. Removed after 4.0.
    @Deprecated
    @JsonProperty("userResetPassword")
    Optional<UserResetPasswordConfiguration> getUserResetPasswordConfig();

    @JsonProperty("superadmin")
    Optional<SuperadminCreatorConfiguration> getSuperadminCreatorConfig();

    @JsonProperty("recaptcha")
    Optional<RecaptchaConfiguration> getRecaptchaConfig();

    // Deprecated: Only read to seed the user registration settings. Removed after 4.0.
    @Deprecated
    @JsonProperty("userRegistration")
    Optional<UserRegistrationConfiguration> getUserRegistrationConfig();

    @JsonProperty("training")
    Optional<TrainingConfiguration> getTrainingConfig();

    @JsonProperty("rating")
    Optional<UserRatingConfiguration> getUserRatingConfig();

    class Builder extends ImmutableJudgelsServerConfiguration.Builder {}
}
