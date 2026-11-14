package judgels.setting;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import java.util.Optional;

@Module
public class SettingModule {
    private final Optional<UserRegistrationConfiguration> userRegistrationConfig;
    private final Optional<UserResetPasswordConfiguration> userResetPasswordConfig;
    private final Optional<GoogleAnalyticsConfiguration> googleAnalyticsConfig;

    public SettingModule(
            Optional<UserRegistrationConfiguration> userRegistrationConfig,
            Optional<UserResetPasswordConfiguration> userResetPasswordConfig,
            Optional<GoogleAnalyticsConfiguration> googleAnalyticsConfig) {

        this.userRegistrationConfig = userRegistrationConfig;
        this.userResetPasswordConfig = userResetPasswordConfig;
        this.googleAnalyticsConfig = googleAnalyticsConfig;
    }

    @Provides
    @Singleton
    SettingCreator settingCreator(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            SettingStore settingStore) {
        return unitOfWorkAwareProxyFactory.create(
                SettingCreator.class,
                new Class<?>[]{SettingStore.class, Optional.class, Optional.class, Optional.class},
                new Object[]{settingStore, userRegistrationConfig, userResetPasswordConfig, googleAnalyticsConfig});
    }
}
