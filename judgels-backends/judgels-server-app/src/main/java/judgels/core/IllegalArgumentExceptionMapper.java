package judgels.core;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import judgels.core.api.JudgelsApiError;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response
                .status(Status.BAD_REQUEST)
                .entity(new JudgelsApiError(Status.BAD_REQUEST.getStatusCode(), exception.getMessage(), null))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
