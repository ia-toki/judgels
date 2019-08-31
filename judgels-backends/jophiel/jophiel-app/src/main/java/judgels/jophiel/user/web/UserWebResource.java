package judgels.jophiel.user.web;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.jophiel.api.user.web.UserWebConfig;
import judgels.jophiel.api.user.web.UserWebService;
import judgels.jophiel.profile.ProfileResource;
import judgels.jophiel.user.me.MyUserResource;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class UserWebResource implements UserWebService {
    private final ActorChecker actorChecker;
    private final MyUserResource myResource;
    private final ProfileResource profileResource;

    @Inject
    public UserWebResource(ActorChecker actorChecker, MyUserResource myResource, ProfileResource profileResource) {
        this.actorChecker = actorChecker;
        this.myResource = myResource;
        this.profileResource = profileResource;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UserWebConfig getWebConfig(AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        return new UserWebConfig.Builder()
                .role(myResource.getMyRole(authHeader))
                .profile(profileResource.getProfile(actorJid))
                .build();
    }
}
