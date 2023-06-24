package judgels.jophiel.user.web;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.role.UserRole;
import judgels.jophiel.api.user.web.UserWebConfig;
import judgels.jophiel.profile.ProfileResource;
import judgels.jophiel.user.me.MyUserResource;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/user-web")
public class UserWebResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected MyUserResource myResource;
    @Inject protected ProfileResource profileResource;
    @Inject protected WebConfiguration webConfig;

    @Inject public UserWebResource() {}

    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserWebConfig getWebConfig(@HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader) {
        UserWebConfig.Builder config = new UserWebConfig.Builder()
                .announcements(webConfig.getAnnouncements());

        if (!authHeader.isPresent()) {
            config.role(new UserRole.Builder().jophiel(JophielRole.GUEST).build());
        } else {
            String actorJid = actorChecker.check(authHeader.get());
            config
                    .role(myResource.getMyRole(authHeader.get()))
                    .profile(profileResource.getProfiles(Set.of(actorJid), Optional.empty()).get(actorJid));
        }
        return config.build();
    }
}
