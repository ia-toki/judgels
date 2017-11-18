package judgels.jophiel.session;

import org.eclipse.jetty.server.session.DefaultSessionIdManager;

public class SessionTokenGenerator {
    private static final DefaultSessionIdManager MANAGER;

    static {
        MANAGER = new DefaultSessionIdManager(null);
        MANAGER.initRandom();
    }

    private SessionTokenGenerator() {}

    public static String newToken() {
        return MANAGER.newSessionId(System.currentTimeMillis());
    }
}
