package judgels.jophiel.session;

import io.dropwizard.hibernate.UnitOfWork;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionCleaner implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionCleaner.class);

    private final Clock clock;
    private final SessionStore sessionStore;

    public SessionCleaner(Clock clock, SessionStore sessionStore) {
        this.clock = clock;
        this.sessionStore = sessionStore;
    }

    @Override
    @UnitOfWork
    public void run() {
        try {
            Instant sixMonthsAgo = clock.instant().minus(Duration.ofDays(6 * 30));
            sessionStore.deleteSessionsOlderThan(sixMonthsAgo);
        } catch (Throwable e) {
            LOGGER.error("Failed to session cleaner", e);
        }
    }
}
