package judgels.service.actor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IpAddressFilter implements ContainerRequestFilter {
    @Context
    private HttpServletRequest httpServletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String address = httpServletRequest.getHeader("X-FORWARDED-FOR");
        if (address == null || "".equals(address)) {
            address = httpServletRequest.getRemoteAddr();
        }
        PerRequestActorProvider.setIpAddress(address);
    }
}
