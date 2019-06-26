package judgels.jophiel.user.superadmin;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import javax.inject.Singleton;
import judgels.jophiel.role.SuperadminRoleStore;
import judgels.jophiel.user.UserStore;

@Module
public class SuperadminModule {
    private SuperadminCreatorConfiguration config;

    public SuperadminModule(SuperadminCreatorConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    SuperadminCreator superadminCreator(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            UserStore userStore,
            SuperadminRoleStore superadminRoleStore) {
        return unitOfWorkAwareProxyFactory.create(
                SuperadminCreator.class,
                new Class<?>[]{UserStore.class, SuperadminRoleStore.class, SuperadminCreatorConfiguration.class},
                new Object[]{userStore, superadminRoleStore, config});
    }
}
