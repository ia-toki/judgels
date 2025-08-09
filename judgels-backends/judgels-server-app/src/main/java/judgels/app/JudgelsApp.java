package judgels.app;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JudgelsApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(JudgelsApp.class);
    private static final String TLX_HASH = "73d675663861b55f68aef37a9edce4e90392c0a3a11ffed47893d774a6203bf6";

    private static JudgelsAppEdition edition = JudgelsAppEdition.FREE;

    private JudgelsApp() {}

    public static void initialize(JudgelsAppConfiguration config) {
        initializeEdition(config);
        if (edition == JudgelsAppEdition.TLX) {
            LOGGER.info("Running as TLX");
        } else {
            LOGGER.info("Running on Free edition");
        }
    }

    public static JudgelsAppEdition getEdition() {
        return edition;
    }

    public static boolean isTLX() {
        return edition == JudgelsAppEdition.TLX;
    }

    // Visible for testing
    static void setEdition(JudgelsAppEdition edition) {
        JudgelsApp.edition = edition;
    }

    private static void initializeEdition(JudgelsAppConfiguration config) {
        if (config.getLicenseKey().isEmpty()) {
            return;
        }

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return;
        }

        byte[] hashbytes = digest.digest(config.getLicenseKey().get().getBytes(StandardCharsets.UTF_8));
        String hash = Hex.encodeHexString(hashbytes);

        if (hash.equals(TLX_HASH)) {
            edition = JudgelsAppEdition.TLX;
        }
    }
}
