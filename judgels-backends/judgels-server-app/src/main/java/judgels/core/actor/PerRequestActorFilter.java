package judgels.core.actor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;

@Provider
public class PerRequestActorFilter implements ContainerRequestFilter {
    @Context
    private HttpServletRequest httpServletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Jetty reuses threads across requests, so the actor from a previous request must not carry over.
        PerRequestActorProvider.clearJid();

        String address = httpServletRequest.getHeader("X-FORWARDED-FOR");
        if (address == null || "".equals(address)) {
            address = httpServletRequest.getRemoteAddr();
        }
        PerRequestActorProvider.setIpAddress(address);
    }
}
