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

        SettingUpdateData data = new SettingUpdateData.Builder()
                .app(new AppSettings.Builder()
                        .name(app.getName().isEmpty() ? "Judgels" : app.getName())
                        .slogan(app.getSlogan().isEmpty() ? "Programming Contest System" : app.getSlogan())
                        .build())
                .build();
        settingStore.updateSettings(data);
    }
}
