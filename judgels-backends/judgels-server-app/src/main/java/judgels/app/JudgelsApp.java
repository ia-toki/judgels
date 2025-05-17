package judgels.app;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JudgelsApp {
    private static final String TLX_MODE_HASH = "73d675663861b55f68aef37a9edce4e90392c0a3a11ffed47893d774a6203bf6";

    private static JudgelsAppMode mode = JudgelsAppMode.JUDGELS;

    private JudgelsApp() {}

    public static void initialize(JudgelsAppConfiguration config) {
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
        String hash = new String(hashbytes);
        if (hash.equals(TLX_MODE_HASH)) {
            mode = JudgelsAppMode.TLX;
        }
    }

    public static JudgelsAppMode getMode() {
        return mode;
    }
}
