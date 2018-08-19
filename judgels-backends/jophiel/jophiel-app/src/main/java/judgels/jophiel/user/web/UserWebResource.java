package judgels.jophiel.user.web;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.jophiel.profile.ProfileResource;
import judgels.jophiel.user.MyResource;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/users/me/web")
public class UserWebResource {
    private final ActorChecker actorChecker;
    private final MyResource myResource;
    private final ProfileResource profileResource;

    @Inject
    public UserWebResource(ActorChecker actorChecker, MyResource myResource, ProfileResource profileResource) {
        this.actorChecker = actorChecker;
        this.myResource = myResource;
        this.profileResource = profileResource;
    }

    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserWebConfig getWebConfig(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        return new UserWebConfig.Builder()
                .role(myResource.getMyRole(authHeader))
                .profile(profileResource.getProfiles(ImmutableSet.of(actorJid)).get(actorJid))
                .build();
    }
}
