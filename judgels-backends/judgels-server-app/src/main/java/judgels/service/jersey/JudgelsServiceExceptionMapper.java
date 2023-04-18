package judgels.service.jersey;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
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
