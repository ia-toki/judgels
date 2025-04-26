package judgels.jophiel.session;

import java.security.SecureRandom;

public class SessionTokenGenerator {
    private static final String POOL = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int LENGTH = 28;

    private SessionTokenGenerator() {}

    public static String newToken() {
        StringBuilder token = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int randomIndex = RANDOM.nextInt(POOL.length());
            token.append(POOL.charAt(randomIndex));
        }
        return token.toString();
    }
}
