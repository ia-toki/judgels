package judgels.service.actor;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
public class IpAddressFilter implements ContainerRequestFilter {
    @Context
    private HttpServletRequest httpServletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        PerRequestActorProvider.setIpAddress(httpServletRequest.getRemoteAddr());
    }
}
