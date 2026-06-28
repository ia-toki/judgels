package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.setting.AppSettings;
import judgels.api.setting.HomeSettings;
import judgels.api.setting.SettingUpdateData;
import judgels.api.user.web.UserWebConfig;
import judgels.setting.SettingClient;
import judgels.user.UserWebClient;
import org.junit.jupiter.api.Test;

class UserWebApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserWebClient userWebClient = createClient(UserWebClient.class);
    private final SettingClient settingClient = createClient(SettingClient.class);

    @Test
    void get_web_config() {
        UserWebConfig anonymousConfig = userWebClient.getPublicWebConfig();
        assertThat(anonymousConfig.getAppName()).isEqualTo(AppSettings.DEFAULT_NAME);
        assertThat(anonymousConfig.getAppSlogan()).isEqualTo(AppSettings.DEFAULT_SLOGAN);
        assertThat(anonymousConfig.getHomeBanner()).isEqualTo(HomeSettings.DEFAULT_BANNER);
        assertThat(anonymousConfig.getRole().getAccount()).isEmpty();
        assertThat(anonymousConfig.getProfile()).isEmpty();

        UserWebConfig adminConfig = userWebClient.getWebConfig(adminToken);
        assertThat(adminConfig.getRole().getAccount()).contains("ADMIN");
        assertThat(adminConfig.getProfile()).isPresent();
        assertThat(adminConfig.getProfile().get().getUsername()).isEqualTo("admin");

        settingClient.updateSettings(adminToken, new SettingUpdateData.Builder()
                .app(new AppSettings.Builder()
                        .name("Updated")
                        .slogan("Updated slogan")
                        .announcement("Hi")
                        .build())
                .home(new HomeSettings.Builder()
                        .banner("<h1>Updated</h1>")
                        .build())
                .build());

        UserWebConfig updatedConfig = userWebClient.getPublicWebConfig();
        assertThat(updatedConfig.getAppName()).isEqualTo("Updated");
        assertThat(updatedConfig.getAppSlogan()).isEqualTo("Updated slogan");
        assertThat(updatedConfig.getAppAnnouncement()).contains("Hi");
        assertThat(updatedConfig.getHomeBanner()).isEqualTo("<h1>Updated</h1>");
    }
}
