package org.iatoki.judgels.jerahmeel.user;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.iatoki.judgels.api.JudgelsAPIClientException;
import org.iatoki.judgels.api.jophiel.JophielPublicAPI;
import org.iatoki.judgels.api.jophiel.JophielUser;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.user.html.addUserView;
import org.iatoki.judgels.jerahmeel.user.html.editUserView;
import org.iatoki.judgels.jerahmeel.user.html.listUsersView;
import org.iatoki.judgels.jerahmeel.user.html.viewUserView;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Authorized(value = "admin")
@Singleton
public final class UserController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;
    private static final String USER = "user";

    private final JophielPublicAPI jophielPublicAPI;
    private final UserService userService;

    @Inject
    public UserController(JophielPublicAPI jophielPublicAPI, UserService userService) {
        this.jophielPublicAPI = jophielPublicAPI;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public Result index() {
        return listUsers(0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listUsers(long pageIndex, String sortBy, String orderBy, String filterString) {
        Page<User> pageOfUsers = userService.getPageOfUsers(pageIndex, PAGE_SIZE, sortBy, orderBy, filterString);

        LazyHtml content = new LazyHtml(listUsersView.render(pageOfUsers, sortBy, orderBy, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("user.list"), new InternalLink(Messages.get("commons.create"), routes.UserController.addUser()), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("user.users"), routes.UserController.index())
        ));
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Users - List");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
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
        JophielUser jophielUser;
        try {
            jophielUser = jophielPublicAPI.findUserByUsername(userCreateData.username);
        } catch (JudgelsAPIClientException e) {
            userAddForm.reject(Messages.get("user.create.error.usernameNotFound"));
            return showCreateUser(userAddForm);
        }

        if (jophielUser == null) {
            userAddForm.reject(Messages.get("user.create.error.usernameNotFound"));
            return showCreateUser(userAddForm);
        }

        if (userService.existsByUserJid(jophielUser.getJid())) {
            userAddForm.reject(Messages.get("user.create.error.userAlreadyExists"));
            return showCreateUser(userAddForm);
        }

        userService.upsertUserFromJophielUser(jophielUser, userCreateData.getRolesAsList(), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.ADD.construct(USER, jophielUser.getJid(), jophielUser.getUsername()));

        return redirect(routes.UserController.index());
    }

    @Transactional(readOnly = true)
    public Result viewUser(long userId) throws UserNotFoundException {
        User user = userService.findUserById(userId);
        LazyHtml content = new LazyHtml(viewUserView.render(user));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("user.user") + " #" + user.getId() + ": " + JidCacheServiceImpl.getInstance().getDisplayName(user.getUserJid()), new InternalLink(Messages.get("commons.update"), routes.UserController.editUser(user.getId())), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("user.users"), routes.UserController.index()),
                new InternalLink(Messages.get("user.view"), routes.UserController.viewUser(user.getId()))
        ));
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "User - View");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
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
        LazyHtml content = new LazyHtml(addUserView.render(userAddForm, jophielPublicAPI.getUserAutocompleteAPIEndpoint()));
        content.appendLayout(c -> headingLayout.render(Messages.get("user.create"), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("user.users"), routes.UserController.index()),
                new InternalLink(Messages.get("user.create"), routes.UserController.addUser())
        ));
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "User - Create");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditUser(Form<UserEditForm> userEditForm, User user) {
        LazyHtml content = new LazyHtml(editUserView.render(userEditForm, user.getId()));
        content.appendLayout(c -> headingLayout.render(Messages.get("user.user") + " #" + user.getId() + ": " + JidCacheServiceImpl.getInstance().getDisplayName(user.getUserJid()), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("user.users"), routes.UserController.index()),
                new InternalLink(Messages.get("user.update"), routes.UserController.editUser(user.getId()))
        ));
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "User - Update");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }
}
