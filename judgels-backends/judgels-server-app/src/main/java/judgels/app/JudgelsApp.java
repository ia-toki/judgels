package judgels.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JudgelsApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(JudgelsApp.class);
    private static final String TLX_MODE = "TLX";

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
        if (config.getMode().filter(TLX_MODE::equals).isPresent()) {
            edition = JudgelsAppEdition.TLX;
        }
    }
}
