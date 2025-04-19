package judgels.service.jersey;

import io.dropwizard.jersey.optional.EmptyOptionalException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class EmptyOptionalExceptionMapper implements ExceptionMapper<EmptyOptionalException> {
    @Override
    public Response toResponse(EmptyOptionalException exception) {
        return Response.noContent().build();
    }
}
