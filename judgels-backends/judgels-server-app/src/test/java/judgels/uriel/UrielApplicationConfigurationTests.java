package judgels.uriel;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import java.io.File;
import java.nio.file.Paths;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

class UrielApplicationConfigurationTests {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<UrielApplicationConfiguration> factory =
            new YamlConfigurationFactory<>(UrielApplicationConfiguration.class, validator, objectMapper, "dw");

    @Test
    void uriel_yml_deserializes() {
        File urielYml = Paths.get("..", "..", "uriel", "uriel-dist", "var", "conf", "uriel.yml.example").toFile();
        assertThatCode(() -> factory.build(urielYml))
                .doesNotThrowAnyException();
    }
}
