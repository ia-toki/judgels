package judgels;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import java.io.File;
import java.nio.file.Paths;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

class JudgelsServerApplicationConfigurationTests {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<JudgelsServerApplicationConfiguration> factory =
            new YamlConfigurationFactory<>(JudgelsServerApplicationConfiguration.class, validator, objectMapper, "dw");

    @Test
    void config_yml_deserializes() {
        File configYml = Paths.get("var", "conf", "judgels-server.yml.example").toFile();
        assertThatCode(() -> factory.build(configYml))
                .doesNotThrowAnyException();
    }
}
