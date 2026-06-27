package judgels.setting;

import jakarta.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import judgels.api.setting.AppSettings;
import judgels.api.setting.HomeSettings;
import judgels.api.setting.SessionSettings;
import judgels.api.setting.SettingUpdateData;
import judgels.api.setting.Settings;
import judgels.persistence.dao.SettingDao;
import judgels.persistence.model.SettingModel;

public class SettingStore {
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
                .build();
    }

    public void updateSettings(SettingUpdateData data) {
        data.getApp().ifPresent(app -> {
            upsertSetting("app_name", app.getName());
            upsertSetting("app_slogan", app.getSlogan());
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
