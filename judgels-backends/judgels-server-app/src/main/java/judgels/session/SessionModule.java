package judgels.session;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import java.time.Clock;
import java.util.Optional;
import judgels.app.JudgelsApp;
import tlx.session.TlxSessionLoginValidator;
import tlx.user.registration.UserRegistrationConfiguration;
import tlx.user.registration.UserRegistrationEmailStore;

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
    SessionLoginValidator sessionLoginValidator(
            Optional<UserRegistrationConfiguration> userRegistrationConfig,
            UserRegistrationEmailStore userRegistrationEmailStore) {
        return JudgelsApp.isTLX()
                ? new TlxSessionLoginValidator(userRegistrationConfig, userRegistrationEmailStore)
                : new JudgelsSessionLoginValidator();
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
