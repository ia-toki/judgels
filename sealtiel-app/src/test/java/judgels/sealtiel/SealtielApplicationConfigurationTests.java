package judgels.sealtiel;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import java.io.File;
import java.nio.file.Paths;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

class SealtielApplicationConfigurationTests {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<SealtielApplicationConfiguration> factory =
            new YamlConfigurationFactory<>(SealtielApplicationConfiguration.class, validator, objectMapper, "dw");

    @Test void can_deserialize_sealtiel_yml() {
        File sealtielYml = Paths.get("..", "sealtiel-dist", "var", "conf", "sealtiel.yml.example").toFile();
        assertThatCode(() -> factory.build(sealtielYml))
                .doesNotThrowAnyException();
    }
}
