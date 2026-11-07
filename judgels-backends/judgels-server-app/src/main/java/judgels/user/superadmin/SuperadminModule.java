package judgels.user.superadmin;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import java.util.Optional;
import judgels.user.UserStore;
import judgels.user.role.UserRoleStore;

@Module
public class SuperadminModule {
    private Optional<SuperadminCreatorConfiguration> config;

    public SuperadminModule(Optional<SuperadminCreatorConfiguration> config) {
        this.config = config;
    }

    @Provides
    @Singleton
    SuperadminCreator superadminCreator(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            UserStore userStore,
            UserRoleStore userRoleStore) {
        return unitOfWorkAwareProxyFactory.create(
                SuperadminCreator.class,
                new Class<?>[]{UserStore.class, UserRoleStore.class, Optional.class},
                new Object[]{userStore, userRoleStore, config});
    }
}
