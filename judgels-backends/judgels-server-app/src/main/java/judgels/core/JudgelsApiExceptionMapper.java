package judgels.core;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import judgels.core.api.JudgelsApiError;
import judgels.core.api.JudgelsApiException;

public class JudgelsApiExceptionMapper implements ExceptionMapper<JudgelsApiException> {
    @Override
    public Response toResponse(JudgelsApiException e) {
        return Response
                .status(e.getCode())
                .entity(new JudgelsApiError(e.getCode(), e.getMessage(), e.getArgs()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
