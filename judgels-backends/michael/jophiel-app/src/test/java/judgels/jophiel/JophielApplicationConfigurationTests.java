package judgels.jophiel;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import java.io.File;
import java.nio.file.Paths;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

class JophielApplicationConfigurationTests {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<JophielApplicationConfiguration> factory =
            new YamlConfigurationFactory<>(JophielApplicationConfiguration.class, validator, objectMapper, "dw");

    @Test
    void jophiel_yml_deserializes() {
        File jophielYml = Paths.get("..", "..", "jophiel", "jophiel-dist", "var", "conf", "jophiel.yml.example").toFile();
        assertThatCode(() -> factory.build(jophielYml))
                .doesNotThrowAnyException();
    }
}
