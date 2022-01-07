package judgels.service.jersey;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import judgels.service.api.JudgelsServiceError;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response
                .status(Status.BAD_REQUEST)
                .entity(new JudgelsServiceError(Status.BAD_REQUEST.getStatusCode(), exception.getMessage(), null))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
