package judgels.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import judgels.JudgelsObjectMappers;
import judgels.core.api.JudgelsApiError;
import judgels.core.api.JudgelsApiException;

public class JudgelsApiErrorDecoder implements ErrorDecoder {
    private static final ObjectMapper MAPPER = JudgelsObjectMappers.OBJECT_MAPPER;

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            JudgelsApiError error = MAPPER.readValue(response.body().asInputStream(), JudgelsApiError.class);
            return new JudgelsApiException(
                    jakarta.ws.rs.core.Response.Status.fromStatusCode(error.getCode()),
                    error.getMessage(),
                    error.getArgs());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
