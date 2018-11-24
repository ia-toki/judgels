package judgels.service.jersey;

import io.dropwizard.jersey.optional.EmptyOptionalException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

// https://github.com/palantir/conjure-java-runtime/issues/427
@Provider
public class EmptyOptionalExceptionMapper implements ExceptionMapper<EmptyOptionalException> {
    @Override
    public Response toResponse(EmptyOptionalException exception) {
        return Response.noContent().build();
    }
}
