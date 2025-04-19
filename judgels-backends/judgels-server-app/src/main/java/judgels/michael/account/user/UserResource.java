package judgels.michael.account.user;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.common.View;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.user.UserCreator;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.info.UserInfoStore;
import judgels.michael.account.BaseAccountResource;
import judgels.michael.template.HtmlTemplate;
import judgels.persistence.api.Page;

@Path("/accounts/users")
public class UserResource extends BaseAccountResource {
    private static final int PAGE_SIZE = 250;

    @Inject protected UserStore userStore;
    @Inject protected UserInfoStore userInfoStore;
    @Inject protected SessionStore sessionStore;
    @Inject protected UserCreator userCreator;

    @Inject public UserResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listUsers(
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

    @GET
    @Path("/upsert")
    @UnitOfWork(readOnly = true)
    public View editUsers(@Context HttpServletRequest req) {
        Actor actor = actorChecker.check(req);
        checkAllowed(userRoleChecker.canAdminister(actor.getUserJid()));

        return renderEditUsers(actor, new UpsertUsersForm());
    }

    @POST
    @Path("/upsert")
    @UnitOfWork
    public View updateUsers(@Context HttpServletRequest req, @BeanParam UpsertUsersForm form) throws IOException {
        Actor actor = actorChecker.check(req);
        checkAllowed(userRoleChecker.canAdminister(actor.getUserJid()));

        UserCreator.UpsertUsersResult result = userCreator.upsertUsers(form.csv);
        if (result.errorMessage.isPresent()) {
            form.globalError = result.errorMessage.get();
            return renderEditUsers(actor, form);
        }

        HtmlTemplate template = newUsersTemplate(actor);
        template.setActiveSecondaryTab("upsert");
        return new UpsertUsersSuccessView(template, result.createdUsernames, result.updatedUsernames);
    }

    @GET
    @Path("/{userId}")
    @UnitOfWork(readOnly = true)
    public View viewUser(@Context HttpServletRequest req, @PathParam("userId") int userId) {
        Actor actor = actorChecker.check(req);
        checkAllowed(userRoleChecker.canAdminister(actor.getUserJid()));

        User user = checkFound(userStore.getUserById(userId));
        UserInfo info = userInfoStore.getInfo(user.getJid());

        HtmlTemplate template = newUsersTemplate(actor);
        template.setActiveSecondaryTab("view");
        return new ViewUserView(template, user, info);
    }

    private View renderEditUsers(Actor actor, UpsertUsersForm form) {
        HtmlTemplate template = newUsersTemplate(actor);
        template.setActiveSecondaryTab("upsert");
        return new UpsertUsersView(template, form);
    }

    private HtmlTemplate newUsersTemplate(Actor actor) {
        HtmlTemplate template = super.newAccountsTemplate(actor);
        template.setTitle("Users");
        template.setActiveMainTab("users");
        template.addSecondaryTab("view", "View", "/accounts/users");
        template.addSecondaryTab("upsert", "Bulk Create / Update", "/accounts/users/upsert");
        return template;
    }
}
