package judgels.jophiel.user.master;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.util.Set;
import javax.inject.Singleton;
import judgels.jophiel.user.UserStore;

@Module
public class MasterUsersModule {
    private final Set<String> masterUsers;

    public MasterUsersModule(Set<String> masterUsers) {
        this.masterUsers = masterUsers;
    }

    @Provides
    @Singleton
    MasterUsersCreator masterUsersCreator(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            UserStore userStore) {

        return unitOfWorkAwareProxyFactory.create(
                MasterUsersCreator.class,
                new Class<?>[]{UserStore.class, Set.class},
                new Object[]{userStore, masterUsers});
    }
}
