package judgels.uriel.admin;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.role.Role;
import judgels.jophiel.api.user.me.MyUserService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.admin.AdminService;
import judgels.uriel.role.AdminRoleStore;

public class AdminResource implements AdminService {
    private final AdminRoleStore roleStore;
    private final MyUserService myUserService;
    private final UserSearchService searchService;

    @Inject
    public AdminResource(AdminRoleStore roleStore, MyUserService myUserService, UserSearchService searchService) {
        this.roleStore = roleStore;
        this.myUserService = myUserService;
        this.searchService = searchService;
    }

    @Override
    @UnitOfWork
    public void upsertAdmin(AuthHeader authHeader, String username) {
        Role role = myUserService.getMyRole(authHeader);
        checkAllowed(role == Role.SUPERADMIN);

        String userJid = checkFound(
                Optional.ofNullable(searchService.translateUsernamesToJids(ImmutableSet.of(username)).get(username)));
        roleStore.upsertAdmin(userJid);
    }
}
