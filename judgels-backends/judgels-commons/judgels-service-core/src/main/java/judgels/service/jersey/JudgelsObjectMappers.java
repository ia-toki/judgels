package judgels.service.jersey;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JudgelsObjectMappers {
    private JudgelsObjectMappers() {}

    public static ObjectMapper configure(ObjectMapper mapper) {
        mapper
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);

        return mapper;
    }
}
