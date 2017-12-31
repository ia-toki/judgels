package judgels.jophiel.user.superadmin;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import javax.inject.Singleton;
import judgels.jophiel.user.UserStore;

@Module
public class SuperadminModule {
    private SuperadminModule() {}

    @Provides
    @Singleton
    static SuperadminCreator superadminCreator(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            UserStore userStore) {

        return unitOfWorkAwareProxyFactory.create(SuperadminCreator.class, UserStore.class, userStore);
    }
}
