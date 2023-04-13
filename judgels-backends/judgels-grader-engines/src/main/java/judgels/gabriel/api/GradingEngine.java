package judgels.gabriel.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public interface GradingEngine {
    String getName();
    GradingConfig createDefaultConfig();
    GradingConfig parseConfig(ObjectMapper mapper, String json) throws IOException;
    GradingResult grade(
            File gradingDir,
            GradingConfig config,
            GradingLanguage language,
            GradingSource source,
            SandboxFactory sandboxFactory) throws GradingException;
}
