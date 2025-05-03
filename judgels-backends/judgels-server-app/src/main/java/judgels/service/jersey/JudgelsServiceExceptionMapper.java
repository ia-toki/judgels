package judgels.service.jersey;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import judgels.service.api.JudgelsServiceError;
import judgels.service.api.JudgelsServiceException;

public class JudgelsServiceExceptionMapper implements ExceptionMapper<JudgelsServiceException> {
    @Override
    public Response toResponse(JudgelsServiceException e) {
        return Response
                .status(e.getCode())
                .entity(new JudgelsServiceError(e.getCode(), e.getMessage(), e.getArgs()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
