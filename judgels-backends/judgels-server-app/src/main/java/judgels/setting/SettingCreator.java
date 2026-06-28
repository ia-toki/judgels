package judgels.setting;

import io.dropwizard.hibernate.UnitOfWork;
import judgels.api.setting.AppSettings;
import judgels.api.setting.HomeSettings;
import judgels.api.setting.SettingUpdateData;
import judgels.api.setting.Settings;

public class SettingCreator {
    private final SettingStore settingStore;

    public SettingCreator(SettingStore settingStore) {
        this.settingStore = settingStore;
    }

    @UnitOfWork
    public void initializeSettings() {
        Settings settings = settingStore.getSettings();
        AppSettings app = settings.getApp();
        HomeSettings home = settings.getHome();

        boolean appInitialized = !app.getName().isEmpty() && !app.getSlogan().isEmpty();
        boolean homeInitialized = !home.getBanner().isEmpty();

        if (appInitialized && homeInitialized) {
            return;
        }

        SettingUpdateData.Builder data = new SettingUpdateData.Builder();
        if (!appInitialized) {
            data.app(new AppSettings.Builder()
                    .name(app.getName().isEmpty() ? AppSettings.DEFAULT_NAME : app.getName())
                    .slogan(app.getSlogan().isEmpty() ? AppSettings.DEFAULT_SLOGAN : app.getSlogan())
                    .announcement(app.getAnnouncement())
                    .build());
        }
        if (!homeInitialized) {
            data.home(new HomeSettings.Builder()
                    .banner(HomeSettings.DEFAULT_BANNER)
                    .build());
        }
        settingStore.updateSettings(data.build());
    }
}
