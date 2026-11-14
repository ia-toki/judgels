package judgels.setting;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Optional;
import judgels.api.setting.Settings;
import judgels.core.mailer.Mailer;
import judgels.core.recaptcha.RecaptchaVerifier;

// Answers whether training's account features are on. It lives in system because system asks too:
// the web config shows the flags, and login checks activation only while registration is on.
public class TrainingAccountChecker {
    private final SettingStore settingStore;
    private final Optional<Mailer> mailer;
    private final Optional<RecaptchaVerifier> recaptchaVerifier;
    private final boolean trainingEnabled;

    @Inject
    public TrainingAccountChecker(
            SettingStore settingStore,
            Optional<Mailer> mailer,
            Optional<RecaptchaVerifier> recaptchaVerifier,
            @Named("trainingEnabled") boolean trainingEnabled) {

        this.settingStore = settingStore;
        this.mailer = mailer;
        this.recaptchaVerifier = recaptchaVerifier;
        this.trainingEnabled = trainingEnabled;
    }

    public boolean isRegistrationEnabled() {
        return isRegistrationEnabled(settingStore.getSettings());
    }

    private boolean isRegistrationEnabled(Settings settings) {
        return trainingEnabled
                && mailer.isPresent()
                && settings.getTrainingAccountRegistration().getEnabled();
    }

    public boolean isRegistrationRecaptchaEnabled() {
        Settings settings = settingStore.getSettings();
        return isRegistrationEnabled(settings)
                && recaptchaVerifier.isPresent()
                && settings.getTrainingAccountRegistration().getRecaptchaEnabled();
    }

    public Optional<String> getRecaptchaSiteKey() {
        if (!isRegistrationRecaptchaEnabled()) {
            return Optional.empty();
        }
        return recaptchaVerifier.map(RecaptchaVerifier::getSiteKey);
    }

    public boolean isPasswordResetEnabled() {
        return trainingEnabled
                && mailer.isPresent()
                && settingStore.getSettings().getTrainingAccountPasswordReset().getEnabled();
    }
}
