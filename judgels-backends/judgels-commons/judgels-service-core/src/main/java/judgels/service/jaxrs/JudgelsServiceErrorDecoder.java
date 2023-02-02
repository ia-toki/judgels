package judgels.service.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import judgels.service.api.JudgelsServiceError;
import judgels.service.api.JudgelsServiceException;

public class JudgelsServiceErrorDecoder implements ErrorDecoder {
    private static final ObjectMapper MAPPER = JudgelsObjectMappers.OBJECT_MAPPER;

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            JudgelsServiceError error = MAPPER.readValue(response.body().asInputStream(), JudgelsServiceError.class);
            return new JudgelsServiceException(
                    javax.ws.rs.core.Response.Status.fromStatusCode(error.getCode()),
                    error.getMessage(),
                    error.getArgs());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
