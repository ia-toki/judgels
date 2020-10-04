package judgels.jophiel.user;

import static com.palantir.conjure.java.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.palantir.conjure.java.api.errors.ErrorType;
import java.util.Comparator;
import java.util.stream.Collectors;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.SessionService;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.api.user.dump.ExportUsersDumpData;
import judgels.jophiel.api.user.dump.UsersDump;
import judgels.service.api.actor.AuthHeader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UserServiceDumpIntegrationTests extends AbstractServiceIntegrationTests {
    private static final UserService userService = createService(UserService.class);
    private static AuthHeader userHeader;

    private static User agus;
    private static User budi;

    @BeforeAll
    static void add_user() {
        agus = userService.createUser(adminHeader, new UserData.Builder()
                .username("agus")
                .email("me@agus.dev")
                .password("password")
                .build()
        );

        budi = userService.createUser(adminHeader, new UserData.Builder()
                .username("budi")
                .email("me@budi.dev")
                .password("password")
                .build()
        );

        userService.createUser(adminHeader, new UserData.Builder()
                .username("cici")
                .email("me@cici.dev")
                .password("password")
                .build()
        );

        SessionService sessionService = createService(SessionService.class);
        userHeader = AuthHeader.of(sessionService.logIn(Credentials.of(agus.getUsername(), "password")).getToken());
    }

    @Test
    void unauthorized_request() {
        assertThatRemoteExceptionThrownBy(
                () -> userService.exportUsers(AuthHeader.of("notExist"), new ExportUsersDumpData.Builder().build()))
                .hasMessageContaining("Judgels:Unauthorized");
    }

    @Test
    void regular_user_is_not_authorized() {
        assertThatRemoteExceptionThrownBy(
                () -> userService.exportUsers(userHeader, new ExportUsersDumpData.Builder().build()))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);
    }

    @Test
    void export() {
        UsersDump usersDump = userService.exportUsers(adminHeader, new ExportUsersDumpData.Builder().usernames(
                ImmutableList.of(agus.getUsername(), budi.getUsername(), "notExist")).build());

        assertThat(
                usersDump
                        .getUsers()
                        .stream()
                        .sorted(Comparator.comparing(User::getUsername))
                        .collect(Collectors.toList())
        ).isEqualTo(
                ImmutableList.of(agus, budi)
        );
    }
}
