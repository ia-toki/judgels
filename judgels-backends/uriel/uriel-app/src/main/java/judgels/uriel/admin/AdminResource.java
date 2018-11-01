package judgels.uriel.admin;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.role.Role;
import judgels.jophiel.api.user.MyService;
import judgels.jophiel.api.user.UserService;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.admin.AdminService;
import judgels.uriel.role.AdminRoleStore;

public class AdminResource implements AdminService {
    private final AdminRoleStore roleStore;
    private final MyService myService;
    private final UserService userService;

    @Inject
    public AdminResource(AdminRoleStore roleStore, MyService myService, UserService userService) {
        this.roleStore = roleStore;
        this.myService = myService;
        this.userService = userService;
    }

    @Override
    @UnitOfWork
    public void upsertAdmin(AuthHeader authHeader, String username) {
        Role role = myService.getMyRole(authHeader);
        checkAllowed(role == Role.SUPERADMIN);

        String userJid = checkFound(
                Optional.ofNullable(userService.translateUsernamesToJids(ImmutableSet.of(username)).get(username)));
        roleStore.upsertAdmin(userJid);
    }
}
