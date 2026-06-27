package judgels.setting;

import io.dropwizard.hibernate.UnitOfWork;
import judgels.api.setting.AppSettings;
import judgels.api.setting.SettingUpdateData;

public class SettingCreator {
    private final SettingStore settingStore;

    public SettingCreator(SettingStore settingStore) {
        this.settingStore = settingStore;
    }

    @UnitOfWork
    public void initializeSettings() {
        AppSettings app = settingStore.getSettings().getApp();

        if (!app.getName().isEmpty() && !app.getSlogan().isEmpty()) {
            return;
        }

        SettingUpdateData data = new SettingUpdateData.Builder()
                .app(new AppSettings.Builder()
                        .name(app.getName().isEmpty() ? AppSettings.DEFAULT_NAME : app.getName())
                        .slogan(app.getSlogan().isEmpty() ? AppSettings.DEFAULT_SLOGAN : app.getSlogan())
                        .build())
                .build();
        settingStore.updateSettings(data);
    }
}
