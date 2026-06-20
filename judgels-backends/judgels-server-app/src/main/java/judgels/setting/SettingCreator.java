package judgels.setting;

import io.dropwizard.hibernate.UnitOfWork;
import judgels.api.setting.AppSettings;
import judgels.api.setting.AppSettingsUpdateData;

public class SettingCreator {
    private final SettingStore settingStore;

    public SettingCreator(SettingStore settingStore) {
        this.settingStore = settingStore;
    }

    @UnitOfWork
    public void initializeSettings() {
        AppSettings app = settingStore.getSettings().getApp();

        AppSettingsUpdateData.Builder data = new AppSettingsUpdateData.Builder();
        if (app.getName().isEmpty()) {
            data.name("Judgels");
        }
        if (app.getSlogan().isEmpty()) {
            data.slogan("Programming Contest System");
        }
        settingStore.updateAppSettings(data.build());
    }
}
