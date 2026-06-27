package judgels.setting;

import jakarta.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import judgels.api.setting.AppSettings;
import judgels.api.setting.SessionSettings;
import judgels.api.setting.SettingUpdateData;
import judgels.api.setting.Settings;
import judgels.persistence.dao.SettingDao;
import judgels.persistence.model.SettingModel;

public class SettingStore {
    private static final String KEY_APP_NAME = "app_name";
    private static final String KEY_APP_SLOGAN = "app_slogan";
    private static final String KEY_SESSION_DISABLE_LOGOUT = "session_disable_logout";
    private static final String KEY_SESSION_MAX_CONCURRENT_SESSIONS_PER_USER =
            "session_max_concurrent_sessions_per_user";

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
                        .name(settings.getOrDefault(KEY_APP_NAME, ""))
                        .slogan(settings.getOrDefault(KEY_APP_SLOGAN, ""))
                        .build())
                .session(new SessionSettings.Builder()
                        .disableLogout(Boolean.parseBoolean(
                                settings.getOrDefault(KEY_SESSION_DISABLE_LOGOUT, "false")))
                        .maxConcurrentSessionsPerUser(Integer.parseInt(
                                settings.getOrDefault(KEY_SESSION_MAX_CONCURRENT_SESSIONS_PER_USER, "-1")))
                        .build())
                .build();
    }

    public void updateSettings(SettingUpdateData data) {
        data.getApp().ifPresent(app -> {
            upsertSetting(KEY_APP_NAME, app.getName());
            upsertSetting(KEY_APP_SLOGAN, app.getSlogan());
        });
        data.getSession().ifPresent(session -> {
            upsertSetting(KEY_SESSION_DISABLE_LOGOUT, String.valueOf(session.getDisableLogout()));
            upsertSetting(
                    KEY_SESSION_MAX_CONCURRENT_SESSIONS_PER_USER,
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
