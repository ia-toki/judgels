package judgels.setting;

import jakarta.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import judgels.api.setting.AppSettings;
import judgels.api.setting.EmailTemplate;
import judgels.api.setting.HomeSettings;
import judgels.api.setting.SessionSettings;
import judgels.api.setting.SettingUpdateData;
import judgels.api.setting.Settings;
import judgels.api.setting.TrainingAccountPasswordResetSettings;
import judgels.api.setting.TrainingAccountRegistrationSettings;
import judgels.persistence.dao.SettingDao;
import judgels.persistence.model.SettingModel;

public class SettingStore {
    static final String APP_GOOGLE_ANALYTICS_TRACKING_ID = "app_google_analytics_tracking_id";
    static final String TRAINING_ACCOUNT_REGISTRATION_ENABLED = "training_account_registration_enabled";
    static final String TRAINING_ACCOUNT_PASSWORD_RESET_ENABLED = "training_account_password_reset_enabled";

    private final SettingDao settingDao;

    @Inject
    public SettingStore(SettingDao settingDao) {
        this.settingDao = settingDao;
    }

    public Settings getSettings() {
        Map<String, String> settings = settingDao.select().all().stream()
                .collect(Collectors.toMap(m -> m.settingKey, m -> m.settingValue));

        return new Settings.Builder()
                .app(new AppSettings.Builder()
                        .name(settings.getOrDefault("app_name", ""))
                        .slogan(settings.getOrDefault("app_slogan", ""))
                        .announcement(Optional.ofNullable(settings.get("app_announcement"))
                                .filter(announcement -> !announcement.isEmpty()))
                        .googleAnalyticsTrackingId(Optional.ofNullable(settings.get(APP_GOOGLE_ANALYTICS_TRACKING_ID))
                                .filter(trackingId -> !trackingId.isEmpty()))
                        .build())
                .home(new HomeSettings.Builder()
                        .banner(settings.getOrDefault("home_banner", ""))
                        .build())
                .session(new SessionSettings.Builder()
                        .disableLogout(Boolean.parseBoolean(
                                settings.getOrDefault("session_disable_logout", "false")))
                        .maxConcurrentSessionsPerUser(Integer.parseInt(
                                settings.getOrDefault("session_max_concurrent_sessions_per_user", "-1")))
                        .build())
                .trainingAccountRegistration(new TrainingAccountRegistrationSettings.Builder()
                        .enabled(Boolean.parseBoolean(settings.getOrDefault(TRAINING_ACCOUNT_REGISTRATION_ENABLED, "false")))
                        .recaptchaEnabled(Boolean.parseBoolean(
                                settings.getOrDefault("training_account_registration_recaptcha_enabled", "false")))
                        .activationEmailTemplate(getEmailTemplate(
                                settings,
                                "training_account_registration_activation_email",
                                TrainingAccountRegistrationSettings.DEFAULT_ACTIVATION_EMAIL_TEMPLATE))
                        .build())
                .trainingAccountPasswordReset(new TrainingAccountPasswordResetSettings.Builder()
                        .enabled(Boolean.parseBoolean(settings.getOrDefault(TRAINING_ACCOUNT_PASSWORD_RESET_ENABLED, "false")))
                        .requestEmailTemplate(getEmailTemplate(
                                settings,
                                "training_account_password_reset_request_email",
                                TrainingAccountPasswordResetSettings.DEFAULT_REQUEST_EMAIL_TEMPLATE))
                        .resetEmailTemplate(getEmailTemplate(
                                settings,
                                "training_account_password_reset_reset_email",
                                TrainingAccountPasswordResetSettings.DEFAULT_RESET_EMAIL_TEMPLATE))
                        .build())
                .build();
    }

    boolean hasSetting(String key) {
        return settingDao.selectByKey(key).isPresent();
    }

    public void updateSettings(SettingUpdateData data) {
        data.getApp().ifPresent(app -> {
            upsertSetting("app_name", app.getName());
            upsertSetting("app_slogan", app.getSlogan());
            upsertSetting("app_announcement", app.getAnnouncement().orElse(""));
            upsertSetting(APP_GOOGLE_ANALYTICS_TRACKING_ID, app.getGoogleAnalyticsTrackingId().orElse(""));
        });
        data.getHome().ifPresent(home -> {
            upsertSetting("home_banner", home.getBanner());
        });
        data.getSession().ifPresent(session -> {
            upsertSetting("session_disable_logout", String.valueOf(session.getDisableLogout()));
            upsertSetting(
                    "session_max_concurrent_sessions_per_user",
                    String.valueOf(session.getMaxConcurrentSessionsPerUser()));
        });
        data.getTrainingAccountRegistration().ifPresent(registration -> {
            upsertSetting(TRAINING_ACCOUNT_REGISTRATION_ENABLED, String.valueOf(registration.getEnabled()));
            upsertSetting(
                    "training_account_registration_recaptcha_enabled",
                    String.valueOf(registration.getRecaptchaEnabled()));
            upsertEmailTemplate("training_account_registration_activation_email", registration.getActivationEmailTemplate());
        });
        data.getTrainingAccountPasswordReset().ifPresent(passwordReset -> {
            upsertSetting(TRAINING_ACCOUNT_PASSWORD_RESET_ENABLED, String.valueOf(passwordReset.getEnabled()));
            upsertEmailTemplate("training_account_password_reset_request_email", passwordReset.getRequestEmailTemplate());
            upsertEmailTemplate("training_account_password_reset_reset_email", passwordReset.getResetEmailTemplate());
        });
    }

    private static EmailTemplate getEmailTemplate(Map<String, String> settings, String keyPrefix, EmailTemplate defaultTemplate) {
        return new EmailTemplate.Builder()
                .subject(settings.getOrDefault(keyPrefix + "_subject", defaultTemplate.getSubject()))
                .body(settings.getOrDefault(keyPrefix + "_body", defaultTemplate.getBody()))
                .build();
    }

    private void upsertEmailTemplate(String keyPrefix, EmailTemplate template) {
        upsertSetting(keyPrefix + "_subject", template.getSubject());
        upsertSetting(keyPrefix + "_body", template.getBody());
    }

    private void upsertSetting(String key, String value) {
        Optional<SettingModel> maybeModel = settingDao.selectByKey(key);
        if (maybeModel.isPresent()) {
            SettingModel model = maybeModel.get();
            model.settingValue = value;
            settingDao.update(model);
        } else {
            SettingModel model = new SettingModel();
            model.settingKey = key;
            model.settingValue = value;
            settingDao.insert(model);
        }
    }
}
