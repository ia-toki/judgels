package judgels.session;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import java.time.Clock;

@Module
public class SessionModule {
    public SessionModule() {}

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
