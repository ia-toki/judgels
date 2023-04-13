package judgels.jerahmeel;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import java.io.File;
import java.nio.file.Paths;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

public class JerahmeelApplicationConfigurationTests {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<JerahmeelApplicationConfiguration> factory =
            new YamlConfigurationFactory<>(JerahmeelApplicationConfiguration.class, validator, objectMapper, "dw");

    @Test
    void jerahmeel_yml_deserializes() {
        File urielYml = Paths.get("..", "..", "jerahmeel", "jerahmeel-dist", "var", "conf", "jerahmeel.yml.example").toFile();
        assertThatCode(() -> factory.build(urielYml))
                .doesNotThrowAnyException();
    }
}
