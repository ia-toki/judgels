package judgels.jophiel.session;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.time.Clock;
import javax.inject.Singleton;

@Module
public class SessionModule {
    private final SessionConfiguration config;

    public SessionModule(SessionConfiguration config) {
        this.config = config;
    }

    @Provides
    SessionConfiguration sessionConfig() {
        return this.config;
    }

    @Provides
    @Singleton
    SessionCleaner sessionCleaner(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            Clock clock,
            SessionStore sessionStore) {
        return unitOfWorkAwareProxyFactory.create(
                SessionCleaner.class,
                new Class<?>[] {
                        Clock.class,
                        SessionStore.class},
                new Object[] {
                        clock,
                        sessionStore});
    }
}
