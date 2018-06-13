package judgels.service.jersey;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {
    @Override
    public Response toResponse(NotAuthorizedException exception) {
        // This is a hack because currently http-remoting does not support 401 exception :(

        Map<String, String> serializableError = Maps.newHashMap();
        serializableError.put("errorType", "CUSTOM_CLIENT");
        serializableError.put("errorName", "Judgels:Unauthorized");

        return Response
                .status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(serializableError)
                .build();
    }
}
