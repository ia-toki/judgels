package judgels.michael.account.user;

import static judgels.service.ServiceUtils.checkAllowed;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.user.User;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.user.UserStore;
import judgels.michael.account.BaseAccountResource;
import judgels.michael.template.HtmlTemplate;
import judgels.persistence.api.Page;

@Path("/accounts/users")
public class UserResource extends BaseAccountResource {
    private static final int PAGE_SIZE = 250;

    @Inject protected UserStore userStore;
    @Inject protected SessionStore sessionStore;

    @Inject public UserResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listProblems(
            @Context HttpServletRequest req,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        Actor actor = actorChecker.check(req);
        checkAllowed(userRoleChecker.canAdminister(actor.getUserJid()));

        Page<User> users = userStore.getUsers(pageNumber, PAGE_SIZE, Optional.empty(), Optional.empty());

        var userJids = Lists.transform(users.getPage(), User::getJid);
        Map<String, Instant> lastSessionTimesMap = sessionStore.getLatestSessionTimeByUserJids(userJids);

        HtmlTemplate template = newUsersTemplate(actor);
        template.setActiveSecondaryTab("view");
        return new ListUsersView(template, users, lastSessionTimesMap);
    }

    private HtmlTemplate newUsersTemplate(Actor actor) {
        HtmlTemplate template = super.newAccountsTemplate(actor);
        template.setActiveMainTab("users");
        template.addSecondaryTab("view", "View", "/accounts/users");
        return template;
    }
}
