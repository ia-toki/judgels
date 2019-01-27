package org.iatoki.judgels.play.jid;

public final class JidService {

    private static JidService INSTANCE;

    private JidService() {
        // prevent instantiation
    }

    public static JidService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JidService();
        }
        return INSTANCE;
    }

    public String parsePrefix(String jid) {
        int jidLength = "JID".length();
        int prefixLength = 4;

        return jid.substring(jidLength, jidLength + prefixLength);
    }
}
