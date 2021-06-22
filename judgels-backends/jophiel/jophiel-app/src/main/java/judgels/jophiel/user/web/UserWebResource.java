package judgels.jophiel.user.web;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.role.UserRole;
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
    private final WebConfiguration webConfig;

    @Inject
    public UserWebResource(
            ActorChecker actorChecker,
            MyUserResource myResource,
            ProfileResource profileResource,
            WebConfiguration webConfig) {
        this.actorChecker = actorChecker;
        this.myResource = myResource;
        this.profileResource = profileResource;
        this.webConfig = webConfig;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UserWebConfig getWebConfig(Optional<AuthHeader> authHeader) {
        UserWebConfig.Builder config = new UserWebConfig.Builder()
                .announcements(webConfig.getAnnouncements());

        if (!authHeader.isPresent()) {
            config.role(new UserRole.Builder().jophiel(JophielRole.GUEST).build());
        } else {
            String actorJid = actorChecker.check(authHeader.get());
            config
                    .role(myResource.getMyRole(authHeader.get()))
                    .profile(profileResource.getProfile(actorJid));
        }
        return config.build();
    }
}
