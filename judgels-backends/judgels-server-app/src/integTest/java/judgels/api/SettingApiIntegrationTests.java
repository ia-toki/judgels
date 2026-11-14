package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.setting.AppSettings;
import judgels.api.setting.EmailTemplate;
import judgels.api.setting.HomeSettings;
import judgels.api.setting.SettingUpdateData;
import judgels.api.setting.Settings;
import judgels.api.setting.TrainingAccountPasswordResetSettings;
import judgels.api.setting.TrainingAccountRegistrationSettings;
import judgels.client.SettingClient;
import org.junit.jupiter.api.Test;

class SettingApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final SettingClient settingClient = createClient(SettingClient.class);

    @Test
    void get_and_update_settings() {
        Settings settings = settingClient.getSettings(adminToken);
        assertThat(settings.getApp().getName()).isEqualTo(AppSettings.DEFAULT_NAME);
        assertThat(settings.getApp().getSlogan()).isEqualTo(AppSettings.DEFAULT_SLOGAN);
        assertThat(settings.getApp().getAnnouncement()).isEmpty();
        assertThat(settings.getApp().getGoogleAnalyticsTrackingId()).isEmpty();
        assertThat(settings.getMailerConfigured()).isTrue();
        assertThat(settings.getRecaptchaConfigured()).isFalse();
        assertThat(settings.getHome().getBanner()).isEqualTo(HomeSettings.DEFAULT_BANNER);

        Settings updated = settingClient.updateSettings(adminToken, new SettingUpdateData.Builder()
                .app(new AppSettings.Builder()
                        .name("My Judge")
                        .slogan("Train hard")
                        .announcement("Maintenance tonight")
                        .googleAnalyticsTrackingId("G-TEST")
                        .build())
                .home(new HomeSettings.Builder()
                        .banner("<h1>Hello</h1>")
                        .build())
                .build());

        assertThat(updated.getApp().getName()).isEqualTo("My Judge");
        assertThat(updated.getApp().getSlogan()).isEqualTo("Train hard");
        assertThat(updated.getApp().getAnnouncement()).contains("Maintenance tonight");
        assertThat(updated.getApp().getGoogleAnalyticsTrackingId()).contains("G-TEST");
        assertThat(updated.getHome().getBanner()).isEqualTo("<h1>Hello</h1>");

        assertThat(settingClient.getSettings(adminToken)).isEqualTo(updated);
    }

    @Test
    void get_and_update_account_settings() {
        Settings settings = settingClient.getSettings(adminToken);
        assertThat(settings.getTrainingAccountRegistration()).isEqualTo(new TrainingAccountRegistrationSettings.Builder()
                .enabled(false)
                .recaptchaEnabled(false)
                .activationEmailTemplate(TrainingAccountRegistrationSettings.DEFAULT_ACTIVATION_EMAIL_TEMPLATE)
                .build());
        assertThat(settings.getTrainingAccountPasswordReset()).isEqualTo(new TrainingAccountPasswordResetSettings.Builder()
                .enabled(false)
                .requestEmailTemplate(TrainingAccountPasswordResetSettings.DEFAULT_REQUEST_EMAIL_TEMPLATE)
                .resetEmailTemplate(TrainingAccountPasswordResetSettings.DEFAULT_RESET_EMAIL_TEMPLATE)
                .build());

        TrainingAccountRegistrationSettings userRegistration = new TrainingAccountRegistrationSettings.Builder()
                .enabled(true)
                .recaptchaEnabled(false)
                .activationEmailTemplate(EmailTemplate.of("Activate", "Code: {{emailCode}}"))
                .build();
        TrainingAccountPasswordResetSettings userResetPassword = new TrainingAccountPasswordResetSettings.Builder()
                .enabled(true)
                .requestEmailTemplate(EmailTemplate.of("Reset?", "Code: {{emailCode}}"))
                .resetEmailTemplate(EmailTemplate.of("Reset", "Done, {{username}}"))
                .build();
        Settings updated = settingClient.updateSettings(adminToken, new SettingUpdateData.Builder()
                .trainingAccountRegistration(userRegistration)
                .trainingAccountPasswordReset(userResetPassword)
                .build());

        assertThat(updated.getTrainingAccountRegistration()).isEqualTo(userRegistration);
        assertThat(updated.getTrainingAccountPasswordReset()).isEqualTo(userResetPassword);
        assertThat(settingClient.getSettings(adminToken)).isEqualTo(updated);
    }

    @Test
    void reject_invalid_account_settings() {
        Settings before = settingClient.getSettings(adminToken);

        assertBadRequest(() -> settingClient.updateSettings(adminToken, new SettingUpdateData.Builder()
                .trainingAccountRegistration(new TrainingAccountRegistrationSettings.Builder()
                        .enabled(true)
                        .recaptchaEnabled(true)
                        .activationEmailTemplate(TrainingAccountRegistrationSettings.DEFAULT_ACTIVATION_EMAIL_TEMPLATE)
                        .build())
                .build()));

        assertBadRequest(() -> settingClient.updateSettings(adminToken, new SettingUpdateData.Builder()
                .trainingAccountRegistration(new TrainingAccountRegistrationSettings.Builder()
                        .enabled(true)
                        .recaptchaEnabled(false)
                        .activationEmailTemplate(EmailTemplate.of("Activate", "No code"))
                        .build())
                .build()));

        assertBadRequest(() -> settingClient.updateSettings(adminToken, new SettingUpdateData.Builder()
                .trainingAccountPasswordReset(new TrainingAccountPasswordResetSettings.Builder()
                        .enabled(true)
                        .requestEmailTemplate(EmailTemplate.of("Reset?", "No code"))
                        .resetEmailTemplate(TrainingAccountPasswordResetSettings.DEFAULT_RESET_EMAIL_TEMPLATE)
                        .build())
                .build()));

        assertThat(settingClient.getSettings(adminToken)).isEqualTo(before);
    }
}
