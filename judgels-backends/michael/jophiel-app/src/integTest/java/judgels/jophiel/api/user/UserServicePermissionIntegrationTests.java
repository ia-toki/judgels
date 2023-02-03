package judgels.jophiel.api.user;

import static java.util.Optional.empty;

import com.google.common.collect.ImmutableList;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.service.api.actor.AuthHeader;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class UserServicePermissionIntegrationTests extends AbstractServiceIntegrationTests {
    private final UserService userService = createService(UserService.class);

    @Test
    void create_user() {
        assertPermitted(createUser(adminHeader));
        assertForbidden(createUser(userHeader));
    }

    @Test
    void get_user() {
        User andi = createUser("andi");
        AuthHeader andiHeader = getHeader(andi);

        assertPermitted(getUser(adminHeader, andi.getJid()));
        assertPermitted(getUser(andiHeader, andi.getJid()));
        assertForbidden(getUser(userHeader, andi.getJid()));
    }

    @Test
    void get_users() {
        assertPermitted(getUsers(adminHeader));
        assertForbidden(getUsers(userHeader));
    }

    @Test
    void export_users() {
        assertPermitted(exportUsers(adminHeader));
        assertForbidden(exportUsers(userHeader));
    }

    @Test
    void upsert_users() {
        assertPermitted(upsertUsers(adminHeader));
        assertForbidden(upsertUsers(userHeader));
    }

    private ThrowingCallable createUser(AuthHeader authHeader) {
        return () -> userService.createUser(authHeader, new UserData.Builder()
                .username(randomString())
                .password(randomString())
                .email(randomString() + "@domain.com")
                .build());
    }

    private ThrowingCallable getUser(AuthHeader authHeader, String userJid) {
        return () -> userService.getUser(authHeader, userJid);
    }

    private ThrowingCallable getUsers(AuthHeader authHeader) {
        return () -> userService.getUsers(authHeader, empty(), empty(), empty());
    }

    private ThrowingCallable exportUsers(AuthHeader authHeader) {
        return () -> userService.exportUsers(authHeader, ImmutableList.of());
    }

    private ThrowingCallable upsertUsers(AuthHeader authHeader) {
        return () -> userService.upsertUsers(authHeader, "country,name,email,username,password\n");
    }
}
