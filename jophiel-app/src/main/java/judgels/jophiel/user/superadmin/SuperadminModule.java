package judgels.jophiel.user.superadmin;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import javax.inject.Singleton;
import judgels.jophiel.role.RoleStore;
import judgels.jophiel.user.UserStore;

@Module
public class SuperadminModule {
    private SuperadminModule() {}

    @Provides
    @Singleton
    static SuperadminCreator superadminCreator(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            UserStore userStore,
            RoleStore roleStore) {

        return unitOfWorkAwareProxyFactory.create(
                SuperadminCreator.class,
                new Class<?>[]{UserStore.class, RoleStore.class},
                new Object[]{userStore, roleStore});
    }
}
