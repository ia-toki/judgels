package judgels.setting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import judgels.api.setting.AppSettings;
import judgels.api.setting.HomeSettings;
import judgels.api.setting.SessionSettings;
import judgels.api.setting.Settings;
import judgels.api.setting.TrainingAccountPasswordResetSettings;
import judgels.api.setting.TrainingAccountRegistrationSettings;
import judgels.core.mailer.Mailer;
import judgels.core.recaptcha.RecaptchaVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrainingAccountCheckerTests {
    private static final Optional<Mailer> MAILER = Optional.of(mock(Mailer.class));

    private SettingStore settingStore;
    private Optional<RecaptchaVerifier> recaptchaVerifier;

    @BeforeEach
    void before() {
        RecaptchaVerifier verifier = mock(RecaptchaVerifier.class);
        when(verifier.getSiteKey()).thenReturn("site-key");
        recaptchaVerifier = Optional.of(verifier);

        settingStore = mock(SettingStore.class);
        when(settingStore.getSettings()).thenReturn(settingsWithEverythingOn());
    }

    @Test
    void everything_is_on_with_training_mailer_recaptcha_and_settings() {
        TrainingAccountChecker checker = new TrainingAccountChecker(settingStore, MAILER, recaptchaVerifier, true);

        assertThat(checker.isRegistrationEnabled()).isTrue();
        assertThat(checker.isRegistrationRecaptchaEnabled()).isTrue();
        assertThat(checker.getRecaptchaSiteKey()).contains("site-key");
        assertThat(checker.isPasswordResetEnabled()).isTrue();
    }

    @Test
    void everything_is_off_when_training_is_off() {
        TrainingAccountChecker checker = new TrainingAccountChecker(settingStore, MAILER, recaptchaVerifier, false);

        assertThat(checker.isRegistrationEnabled()).isFalse();
        assertThat(checker.isRegistrationRecaptchaEnabled()).isFalse();
        assertThat(checker.getRecaptchaSiteKey()).isEmpty();
        assertThat(checker.isPasswordResetEnabled()).isFalse();
    }

    @Test
    void everything_is_off_without_a_mailer() {
        TrainingAccountChecker checker =
                new TrainingAccountChecker(settingStore, Optional.empty(), recaptchaVerifier, true);

        assertThat(checker.isRegistrationEnabled()).isFalse();
        assertThat(checker.getRecaptchaSiteKey()).isEmpty();
        assertThat(checker.isPasswordResetEnabled()).isFalse();
    }

    @Test
    void recaptcha_is_off_without_the_recaptcha_config() {
        TrainingAccountChecker checker = new TrainingAccountChecker(settingStore, MAILER, Optional.empty(), true);

        assertThat(checker.isRegistrationEnabled()).isTrue();
        assertThat(checker.isRegistrationRecaptchaEnabled()).isFalse();
        assertThat(checker.getRecaptchaSiteKey()).isEmpty();
    }

    private static Settings settingsWithEverythingOn() {
        return new Settings.Builder()
                .app(new AppSettings.Builder()
                        .name(AppSettings.DEFAULT_NAME)
                        .slogan(AppSettings.DEFAULT_SLOGAN)
                        .build())
                .home(new HomeSettings.Builder()
                        .banner(HomeSettings.DEFAULT_BANNER)
                        .build())
                .session(new SessionSettings.Builder()
                        .disableLogout(false)
                        .maxConcurrentSessionsPerUser(-1)
                        .build())
                .trainingAccountRegistration(new TrainingAccountRegistrationSettings.Builder()
                        .enabled(true)
                        .recaptchaEnabled(true)
                        .activationEmailTemplate(TrainingAccountRegistrationSettings.DEFAULT_ACTIVATION_EMAIL_TEMPLATE)
                        .build())
                .trainingAccountPasswordReset(new TrainingAccountPasswordResetSettings.Builder()
                        .enabled(true)
                        .requestEmailTemplate(TrainingAccountPasswordResetSettings.DEFAULT_REQUEST_EMAIL_TEMPLATE)
                        .resetEmailTemplate(TrainingAccountPasswordResetSettings.DEFAULT_RESET_EMAIL_TEMPLATE)
                        .build())
                .build();
    }
}
