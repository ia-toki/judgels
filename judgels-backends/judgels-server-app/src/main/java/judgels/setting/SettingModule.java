package judgels.setting;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import java.util.Optional;
import judgels.user.account.UserResetPasswordConfiguration;
import judgels.user.registration.UserRegistrationConfiguration;

@Module
public class SettingModule {
    private final Optional<UserRegistrationConfiguration> userRegistrationConfig;
    private final Optional<UserResetPasswordConfiguration> userResetPasswordConfig;

    public SettingModule(
            Optional<UserRegistrationConfiguration> userRegistrationConfig,
            Optional<UserResetPasswordConfiguration> userResetPasswordConfig) {

        this.userRegistrationConfig = userRegistrationConfig;
        this.userResetPasswordConfig = userResetPasswordConfig;
    }

    @Provides
    @Singleton
    SettingCreator settingCreator(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            SettingStore settingStore) {
        return unitOfWorkAwareProxyFactory.create(
                SettingCreator.class,
                new Class<?>[]{SettingStore.class, Optional.class, Optional.class},
                new Object[]{settingStore, userRegistrationConfig, userResetPasswordConfig});
    }
}
