package judgels.gabriel.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class GabrielObjectMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModules(new Jdk8Module(), new GuavaModule());

    private GabrielObjectMapper() {}

    public static ObjectMapper getInstance() {
        return MAPPER;
    }
}
