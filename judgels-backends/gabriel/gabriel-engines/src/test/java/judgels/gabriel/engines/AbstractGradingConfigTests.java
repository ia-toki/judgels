package judgels.gabriel.engines;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.io.IOException;
import java.io.InputStream;
import judgels.gabriel.api.GradingConfig;

public abstract class AbstractGradingConfigTests {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModules(new Jdk8Module(), new GuavaModule());

    protected void assertConfig(
            String filename,
            Class<? extends GradingConfig> clazz,
            GradingConfig config) throws IOException {

        InputStream stream = AbstractGradingConfigTests.class.getClassLoader()
                .getResourceAsStream("configs/" + filename + ".json");

        GradingConfig config1 = MAPPER.readValue(stream, clazz);
        assertThat(config1).isEqualTo(config);

        String str = MAPPER.writeValueAsString(config1);
        GradingConfig config2 = MAPPER.readValue(str, clazz);
        assertThat(config2).isEqualTo(config);
    }
}
