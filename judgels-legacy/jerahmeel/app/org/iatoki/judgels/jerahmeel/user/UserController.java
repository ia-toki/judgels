package org.iatoki.judgels.jerahmeel.user;

import com.google.common.collect.ImmutableSet;
import judgels.jophiel.api.user.search.UserSearchService;
import org.apache.commons.lang3.StringUtils;
import org.iatoki.judgels.jerahmeel.AbstractJerahmeelController;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.user.html.addUserView;
import org.iatoki.judgels.jerahmeel.user.html.editUserView;
import org.iatoki.judgels.jerahmeel.user.html.listUsersView;
import org.iatoki.judgels.jerahmeel.user.html.viewUserView;
import org.iatoki.judgels.jophiel.JophielClientControllerUtils;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Authorized(value = "admin")
@Singleton
public final class UserController extends AbstractJerahmeelController {

    private static final long PAGE_SIZE = 20;
    private static final String USER = "user";

    private final UserSearchService userSearchService;
    private final UserService userService;

    @Inject
    public UserController(UserSearchService userSearchService, UserService userService) {
        this.userSearchService = userSearchService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public Result index() {
        return listUsers(0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listUsers(long pageIndex, String sortBy, String orderBy, String filterString) {
        Page<User> pageOfUsers = userService.getPageOfUsers(pageIndex, PAGE_SIZE, sortBy, orderBy, filterString);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listUsersView.render(pageOfUsers, sortBy, orderBy, filterString));
        template.setMainTitle(Messages.get("user.list"));
        template.addMainButton(Messages.get("commons.create"), routes.UserController.addUser());
        template.setPageTitle("Users - List");

        return renderTemplate(template);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result addUser() {
        UserAddForm userAddData = new UserAddForm();
        userAddData.roles = StringUtils.join(JerahmeelUtils.getDefaultRoles(), ",");
        Form<UserAddForm> userCreateForm = Form.form(UserAddForm.class).fill(userAddData);

        return showCreateUser(userCreateForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddUser() {
        Form<UserAddForm> userAddForm = Form.form(UserAddForm.class).bindFromRequest();

        if (formHasErrors(userAddForm)) {
            return showCreateUser(userAddForm);
        }

        UserAddForm userCreateData = userAddForm.get();

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(ImmutableSet.of(userCreateData.username));

        if (!usernameToJidMap.containsKey(userCreateData.username)) {
            userAddForm.reject(Messages.get("user.create.error.usernameNotFound"));
            return showCreateUser(userAddForm);
        }

        String userJid = usernameToJidMap.get(userCreateData.username);

        if (userService.existsByUserJid(userJid)) {
            userAddForm.reject(Messages.get("user.create.error.userAlreadyExists"));
            return showCreateUser(userAddForm);
        }

        userService.upsertUserFromJophielUser(userJid, userCreateData.username, userCreateData.getRolesAsList(), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.ADD.construct(USER, userJid, userCreateData.username));

        return redirect(routes.UserController.index());
    }

    @Transactional(readOnly = true)
    public Result viewUser(long userId) throws UserNotFoundException {
        User user = userService.findUserById(userId);
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(viewUserView.render(user));
        template.setMainTitle(Messages.get("user.user") + " #" + user.getId() + ": " + JidCacheServiceImpl.getInstance().getDisplayName(user.getUserJid()));
        template.addMainButton(Messages.get("commons.update"), routes.UserController.editUser(user.getId()));
        template.markBreadcrumbLocation(Messages.get("user.view"), routes.UserController.viewUser(user.getId()));
        template.setPageTitle("User - View");

        return renderTemplate(template);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editUser(long userId) throws UserNotFoundException {
        User user = userService.findUserById(userId);
        UserEditForm userEditForm = new UserEditForm();
        userEditForm.roles = StringUtils.join(user.getRoles(), ",");
        Form<UserEditForm> form = Form.form(UserEditForm.class).fill(userEditForm);

        return showEditUser(form, user);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditUser(long userId) throws UserNotFoundException {
        User user = userService.findUserById(userId);
        Form<UserEditForm> userEditForm = Form.form(UserEditForm.class).bindFromRequest();

        if (formHasErrors(userEditForm)) {
            return showEditUser(userEditForm, user);
        }

        UserEditForm userEditData = userEditForm.get();
        userService.updateUser(user.getUserJid(), userEditData.getRolesAsList(), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT.construct(USER, user.getUserJid(), JidCacheServiceImpl.getInstance().getDisplayName(user.getUserJid())));

        return redirect(routes.UserController.index());
    }

    @Transactional
    public Result removeUser(long userId) throws UserNotFoundException {
        User user = userService.findUserById(userId);
        userService.deleteUser(user.getUserJid());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.REMOVE.construct(USER, user.getUserJid(), JidCacheServiceImpl.getInstance().getDisplayName(user.getUserJid())));

        return redirect(routes.UserController.index());
    }

    private Result showCreateUser(Form<UserAddForm> userAddForm) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(addUserView.render(userAddForm, JophielClientControllerUtils.getInstance().getUserAutocompleteAPIEndpoint()));
        template.setMainTitle(Messages.get("user.create"));
        template.markBreadcrumbLocation(Messages.get("user.create"), routes.UserController.addUser());
        template.setPageTitle("User - Create");

        return renderTemplate(template);
    }

    private Result showEditUser(Form<UserEditForm> userEditForm, User user) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editUserView.render(userEditForm, user.getId()));
        template.setMainTitle(Messages.get("user.user") + " #" + user.getId() + ": " + JidCacheServiceImpl.getInstance().getDisplayName(user.getUserJid()));
        template.markBreadcrumbLocation(Messages.get("user.update"), routes.UserController.editUser(user.getId()));
        template.setPageTitle("User - Update");

        return renderTemplate(template);
    }

    protected Result renderTemplate(HtmlTemplate template) {
        template.markBreadcrumbLocation(Messages.get("user.users"), routes.UserController.index());

        return super.renderTemplate(template);
    }
}
