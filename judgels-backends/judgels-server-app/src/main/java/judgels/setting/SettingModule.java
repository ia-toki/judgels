package judgels.setting;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;

@Module
public class SettingModule {
    @Provides
    @Singleton
    SettingCreator settingCreator(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            SettingStore settingStore) {
        return unitOfWorkAwareProxyFactory.create(
                SettingCreator.class,
                new Class<?>[]{SettingStore.class},
                new Object[]{settingStore});
    }
}
