package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.setting.AppSettings;
import judgels.api.setting.HomeSettings;
import judgels.api.setting.SettingUpdateData;
import judgels.api.setting.Settings;
import judgels.setting.SettingClient;
import org.junit.jupiter.api.Test;

class SettingApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final SettingClient settingClient = createClient(SettingClient.class);

    @Test
    void get_and_update_settings() {
        Settings settings = settingClient.getSettings(adminToken);
        assertThat(settings.getApp().getName()).isEqualTo(AppSettings.DEFAULT_NAME);
        assertThat(settings.getApp().getSlogan()).isEqualTo(AppSettings.DEFAULT_SLOGAN);
        assertThat(settings.getApp().getAnnouncement()).isEmpty();
        assertThat(settings.getHome().getBanner()).isEqualTo(HomeSettings.DEFAULT_BANNER);

        Settings updated = settingClient.updateSettings(adminToken, new SettingUpdateData.Builder()
                .app(new AppSettings.Builder()
                        .name("My Judge")
                        .slogan("Train hard")
                        .announcement("Maintenance tonight")
                        .build())
                .home(new HomeSettings.Builder()
                        .banner("<h1>Hello</h1>")
                        .build())
                .build());

        assertThat(updated.getApp().getName()).isEqualTo("My Judge");
        assertThat(updated.getApp().getSlogan()).isEqualTo("Train hard");
        assertThat(updated.getApp().getAnnouncement()).contains("Maintenance tonight");
        assertThat(updated.getHome().getBanner()).isEqualTo("<h1>Hello</h1>");

        assertThat(settingClient.getSettings(adminToken)).isEqualTo(updated);
    }
}
