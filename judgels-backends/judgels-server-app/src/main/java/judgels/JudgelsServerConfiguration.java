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
import judgels.rating.RatingConfiguration;
import judgels.setting.GoogleAnalyticsConfiguration;
import judgels.setting.UserRegistrationConfiguration;
import judgels.setting.UserResetPasswordConfiguration;
import judgels.training.TrainingConfiguration;
import judgels.user.superadmin.SuperadminConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJudgelsServerConfiguration.class)
public interface JudgelsServerConfiguration {
    Path getBaseDataDir();

    @JsonProperty("auth")
    Optional<AuthConfiguration> getAuthConfig();

    @JsonProperty("rabbitmq")
    Optional<RabbitMQConfiguration> getRabbitMQConfig();

    @JsonProperty("mailer")
    Optional<MailerConfiguration> getMailerConfig();

    @JsonProperty("recaptcha")
    Optional<RecaptchaConfiguration> getRecaptchaConfig();

    @JsonProperty("superadmin")
    Optional<SuperadminConfiguration> getSuperadminConfig();

    @JsonProperty("grading")
    JudgelsServerGradingConfiguration getGradingConfig();

    @JsonProperty("rating")
    Optional<RatingConfiguration> getRatingConfig();

    @JsonProperty("training")
    Optional<TrainingConfiguration> getTrainingConfig();

    // Deprecated: Only read to seed the Google Analytics tracking ID setting. Removed after 4.0.
    @Deprecated
    @JsonProperty("googleAnalytics")
    Optional<GoogleAnalyticsConfiguration> getGoogleAnalyticsConfig();

    // Deprecated: Only read to seed the user registration settings. Removed after 4.0.
    @Deprecated
    @JsonProperty("userRegistration")
    Optional<UserRegistrationConfiguration> getUserRegistrationConfig();

    // Deprecated: Only read to seed the user reset password settings. Removed after 4.0.
    @Deprecated
    @JsonProperty("userResetPassword")
    Optional<UserResetPasswordConfiguration> getUserResetPasswordConfig();

    class Builder extends ImmutableJudgelsServerConfiguration.Builder {}
}
