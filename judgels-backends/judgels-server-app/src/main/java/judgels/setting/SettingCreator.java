package judgels.setting;

import static judgels.setting.SettingStore.USER_REGISTRATION_ENABLED;
import static judgels.setting.SettingStore.USER_RESET_PASSWORD_ENABLED;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import judgels.api.setting.AppSettings;
import judgels.api.setting.HomeSettings;
import judgels.api.setting.SettingUpdateData;
import judgels.api.setting.Settings;
import judgels.api.setting.UserRegistrationSettings;
import judgels.api.setting.UserResetPasswordSettings;
import judgels.user.account.UserResetPasswordConfiguration;
import judgels.user.registration.UserRegistrationConfiguration;

public class SettingCreator {
    private final SettingStore settingStore;

    // Deprecated: the config these come from is only read to seed the settings, and goes away after 4.0.
    private final Optional<UserRegistrationConfiguration> userRegistrationConfig;
    private final Optional<UserResetPasswordConfiguration> userResetPasswordConfig;

    public SettingCreator(
            SettingStore settingStore,
            Optional<UserRegistrationConfiguration> userRegistrationConfig,
            Optional<UserResetPasswordConfiguration> userResetPasswordConfig) {

        this.settingStore = settingStore;
        this.userRegistrationConfig = userRegistrationConfig;
        this.userResetPasswordConfig = userResetPasswordConfig;
    }

    @UnitOfWork
    public void initializeSettings() {
        Settings settings = settingStore.getSettings();
        AppSettings app = settings.getApp();
        HomeSettings home = settings.getHome();

        boolean appInitialized = !app.getName().isEmpty() && !app.getSlogan().isEmpty();
        boolean homeInitialized = !home.getBanner().isEmpty();

        SettingUpdateData.Builder data = new SettingUpdateData.Builder();
        if (!appInitialized) {
            data.app(new AppSettings.Builder()
                    .name(app.getName().isEmpty() ? AppSettings.DEFAULT_NAME : app.getName())
                    .slogan(app.getSlogan().isEmpty() ? AppSettings.DEFAULT_SLOGAN : app.getSlogan())
                    .announcement(app.getAnnouncement())
                    .build());
        }
        if (!homeInitialized) {
            data.home(new HomeSettings.Builder()
                    .banner(HomeSettings.DEFAULT_BANNER)
                    .build());
        }

        userRegistrationConfig
                .filter(UserRegistrationConfiguration::getEnabled)
                .filter(c -> !settingStore.hasSetting(USER_REGISTRATION_ENABLED))
                .ifPresent(c -> data.userRegistration(new UserRegistrationSettings.Builder()
                        .enabled(true)
                        .recaptchaEnabled(c.getUseRecaptcha())
                        .activationEmailTemplate(c.getActivationEmailTemplate())
                        .build()));
        userResetPasswordConfig
                .filter(UserResetPasswordConfiguration::getEnabled)
                .filter(c -> !settingStore.hasSetting(USER_RESET_PASSWORD_ENABLED))
                .ifPresent(c -> data.userResetPassword(new UserResetPasswordSettings.Builder()
                        .enabled(true)
                        .requestEmailTemplate(c.getRequestEmailTemplate())
                        .resetEmailTemplate(c.getResetEmailTemplate())
                        .build()));

        settingStore.updateSettings(data.build());
    }
}
