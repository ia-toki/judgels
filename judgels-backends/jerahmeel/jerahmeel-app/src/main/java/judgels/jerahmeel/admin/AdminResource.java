package judgels.jerahmeel.admin;

import static com.google.common.base.Preconditions.checkArgument;
import static judgels.service.ServiceUtils.checkAllowed;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.admin.Admin;
import judgels.jerahmeel.api.admin.AdminService;
import judgels.jerahmeel.api.admin.AdminsDeleteResponse;
import judgels.jerahmeel.api.admin.AdminsResponse;
import judgels.jerahmeel.api.admin.AdminsUpsertResponse;
import judgels.jerahmeel.role.AdminRoleStore;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.role.Role;
import judgels.jophiel.api.user.me.MyUserService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.persistence.api.Page;
import judgels.service.api.actor.AuthHeader;

public class AdminResource implements AdminService {
    private final AdminRoleStore roleStore;
    private final MyUserService myUserService;
    private final UserSearchService userSearchService;
    private final ProfileService profileService;

    @Inject
    public AdminResource(
            AdminRoleStore roleStore,
            MyUserService myUserService,
            UserSearchService userSearchService,
            ProfileService profileService) {

        this.roleStore = roleStore;
        this.myUserService = myUserService;
        this.userSearchService = userSearchService;
        this.profileService = profileService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public AdminsResponse getAdmins(AuthHeader authHeader, Optional<Integer> page) {
        Role role = myUserService.getMyRole(authHeader);
        checkAllowed(role == Role.SUPERADMIN);

        Page<Admin> admins = roleStore.getAdmins(page);
        Set<String> userJids = admins.getPage().stream().map(Admin::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = userJids.isEmpty()
                ? Collections.emptyMap()
                : profileService.getProfiles(userJids);

        return new AdminsResponse.Builder()
                .data(admins)
                .profilesMap(profilesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public AdminsUpsertResponse upsertAdmins(AuthHeader authHeader, Set<String> usernames) {
        Role role = myUserService.getMyRole(authHeader);
        checkAllowed(role == Role.SUPERADMIN);

        checkArgument(usernames.size() <= 100, "Cannot add more than 100 users.");

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(usernames);

        Set<String> userJids = ImmutableSet.copyOf(usernameToJidMap.values());
        Set<String> insertedAdminUsernames = Sets.newHashSet();
        Set<String> alreadyAdminUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (roleStore.upsertAdmin(userJid)) {
                insertedAdminUsernames.add(username);
            } else {
                alreadyAdminUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = profileService.getProfiles(userJids);
        Map<String, Profile> insertedAdminProfilesMap = insertedAdminUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));
        Map<String, Profile> alreadyAdminProfilesMap = alreadyAdminUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        return new AdminsUpsertResponse.Builder()
                .insertedAdminProfilesMap(insertedAdminProfilesMap)
                .alreadyAdminProfilesMap(alreadyAdminProfilesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public AdminsDeleteResponse deleteAdmins(AuthHeader authHeader, Set<String> usernames) {
        Role role = myUserService.getMyRole(authHeader);
        checkAllowed(role == Role.SUPERADMIN);

        checkArgument(usernames.size() <= 100, "Cannot remove more than 100 users.");

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(usernames);

        Set<String> userJids = ImmutableSet.copyOf(usernameToJidMap.values());
        Set<String> deletedAdminUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (roleStore.deleteAdmin(userJid)) {
                deletedAdminUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = profileService.getProfiles(userJids);
        Map<String, Profile> deletedAdminProfilesMap = deletedAdminUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        return new AdminsDeleteResponse.Builder()
                .deletedAdminProfilesMap(deletedAdminProfilesMap)
                .build();
    }
}
