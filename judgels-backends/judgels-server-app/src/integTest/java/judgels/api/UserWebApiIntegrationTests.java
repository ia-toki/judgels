package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.setting.AppSettings;
import judgels.api.setting.HomeSettings;
import judgels.api.setting.SettingUpdateData;
import judgels.api.setting.UserRegistrationSettings;
import judgels.api.setting.UserResetPasswordSettings;
import judgels.api.user.role.UserRole;
import judgels.api.user.web.UserWebConfig;
import judgels.client.SettingClient;
import judgels.client.UserWebClient;
import org.junit.jupiter.api.Test;

class UserWebApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserWebClient userWebClient = createClient(UserWebClient.class);
    private final SettingClient settingClient = createClient(SettingClient.class);

    @Test
    void get_web_config() {
        UserWebConfig anonymousConfig = userWebClient.getWebConfig();
        assertThat(anonymousConfig.getAppName()).isEqualTo(AppSettings.DEFAULT_NAME);
        assertThat(anonymousConfig.getAppSlogan()).isEqualTo(AppSettings.DEFAULT_SLOGAN);
        assertThat(anonymousConfig.getHomeBanner()).isEqualTo(HomeSettings.DEFAULT_BANNER);
        assertThat(anonymousConfig.getRoles()).isEmpty();
        assertThat(anonymousConfig.getProfile()).isEmpty();
        assertThat(anonymousConfig.getUserRegistrationEnabled()).isFalse();
        assertThat(anonymousConfig.getUserResetPasswordEnabled()).isFalse();

        UserWebConfig adminConfig = userWebClient.getWebConfig(adminToken);
        assertThat(adminConfig.getRoles()).containsExactlyInAnyOrder(
                UserRole.SYSTEM_ADMIN,
                UserRole.PROBLEM_ADMIN,
                UserRole.CONTEST_ADMIN,
                UserRole.TRAINING_ADMIN);
        assertThat(adminConfig.getProfile()).isPresent();
        assertThat(adminConfig.getProfile().get().getUsername()).isEqualTo("admin");

        UserWebConfig superadminConfig = userWebClient.getWebConfig(superadminToken);
        assertThat(superadminConfig.getRoles()).containsExactlyInAnyOrder(UserRole.values());

        settingClient.updateSettings(adminToken, new SettingUpdateData.Builder()
                .app(new AppSettings.Builder()
                        .name("Updated")
                        .slogan("Updated slogan")
                        .announcement("Hi")
                        .build())
                .home(new HomeSettings.Builder()
                        .banner("<h1>Updated</h1>")
                        .build())
                .userRegistration(new UserRegistrationSettings.Builder()
                        .enabled(true)
                        .recaptchaEnabled(false)
                        .activationEmailTemplate(UserRegistrationSettings.DEFAULT_ACTIVATION_EMAIL_TEMPLATE)
                        .build())
                .userResetPassword(new UserResetPasswordSettings.Builder()
                        .enabled(true)
                        .requestEmailTemplate(UserResetPasswordSettings.DEFAULT_REQUEST_EMAIL_TEMPLATE)
                        .resetEmailTemplate(UserResetPasswordSettings.DEFAULT_RESET_EMAIL_TEMPLATE)
                        .build())
                .build());

        UserWebConfig updatedConfig = userWebClient.getWebConfig();
        assertThat(updatedConfig.getAppName()).isEqualTo("Updated");
        assertThat(updatedConfig.getAppSlogan()).isEqualTo("Updated slogan");
        assertThat(updatedConfig.getAppAnnouncement()).contains("Hi");
        assertThat(updatedConfig.getHomeBanner()).isEqualTo("<h1>Updated</h1>");
        assertThat(updatedConfig.getUserRegistrationEnabled()).isTrue();
        assertThat(updatedConfig.getUserResetPasswordEnabled()).isTrue();
    }
}
