package judgels.jophiel.api.user;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.awaitility.Awaitility.await;

import com.google.common.collect.ImmutableSet;
import com.palantir.remoting.api.errors.ErrorType;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.SessionService;
import judgels.persistence.api.Page;
import judgels.service.api.actor.AuthHeader;
import org.junit.jupiter.api.Test;
import org.subethamail.wiser.Wiser;

class UserServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService userService = createService(UserService.class);
    private SessionService sessionService = createService(SessionService.class);

    @Test void basic_flow() {
        assertThatRemoteExceptionThrownBy(() -> userService.getUser(adminHeader, "userJid"))
                .isGeneratedFromErrorType(ErrorType.NOT_FOUND);

        User user = userService.createUser(adminHeader, new UserData.Builder()
                .username("alpha")
                .password("pass")
                .email("email@domain.com")
                .build());

        assertThat(user.getUsername()).isEqualTo("alpha");

        assertThat(userService.getUser(adminHeader, user.getJid())).isEqualTo(user);

        UserProfile userProfile = new UserProfile.Builder()
                .name("Alpha")
                .gender("MALE")
                .nationality("id")
                .homeAddress("address")
                .shirtSize("L")
                .institution("university")
                .country("nation")
                .province("province")
                .city("town")
                .build();
        userService.updateUserProfile(adminHeader, user.getJid(), userProfile);
        assertThat(userService.getUserProfile(adminHeader, user.getJid())).isEqualTo(userProfile);

        User nano = userService.createUser(adminHeader, new UserData.Builder()
                .username("nano")
                .password("pass")
                .email("nano@domain.com")
                .build());
        User budi = userService.createUser(adminHeader, new UserData.Builder()
                .username("budi")
                .password("pass")
                .email("budi@domain.com")
                .build());

        Page<User> users = userService.getUsers(adminHeader, 1, 10);
        assertThat(users.getData()).contains(user, nano, budi);
    }

    @Test void register_flow() {
        Wiser wiser = new Wiser();
        wiser.setPort(2500);
        wiser.start();

        assertThat(userService.usernameExists("beta")).isFalse();
        assertThat(userService.emailExists("beta@domain.com")).isFalse();

        User beta = userService.registerUser(new UserRegistrationData.Builder()
                .username("beta")
                .name("Beta")
                .password("pass")
                .email("beta@domain.com")
                .build());
        Credentials credentials = Credentials.of("beta", "pass");

        String email = readEmail(wiser, 0);

        assertThatRemoteExceptionThrownBy(() -> sessionService.logIn(credentials))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        String emailCode = extractEmailCode(email);

        userService.activateUser(emailCode);
        assertThatCode(() -> sessionService.logIn(credentials))
                .doesNotThrowAnyException();

        assertThat(userService.emailExists("beta@domain.com")).isTrue();

        UserProfile userProfile = userService.getUserProfile(adminHeader, beta.getJid());
        assertThat(userProfile.getName()).contains("Beta");

        wiser.stop();
    }

    @Test void update_password_flow() {
        userService.createUser(adminHeader, new UserData.Builder()
                .username("charlie")
                .password("pass")
                .email("charlie@domain.com")
                .build());
        AuthHeader authHeader = AuthHeader.of(sessionService.logIn(Credentials.of("charlie", "pass")).getToken());

        PasswordUpdateData wrongData = PasswordUpdateData.of("wrongPass", "newPass");
        assertThatRemoteExceptionThrownBy(() -> userService.updateMyPassword(authHeader, wrongData))
                .isGeneratedFromErrorType(ErrorType.INVALID_ARGUMENT);

        assertThatCode(() -> sessionService.logIn(Credentials.of("charlie", "pass")))
                .doesNotThrowAnyException();

        assertThatRemoteExceptionThrownBy(() -> sessionService.logIn(Credentials.of("charlie", "newPass")))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        PasswordUpdateData correctData = PasswordUpdateData.of("pass", "newPass");
        userService.updateMyPassword(authHeader, correctData);

        assertThatRemoteExceptionThrownBy(() -> sessionService.logIn(Credentials.of("charlie", "pass")))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        assertThatCode(() -> sessionService.logIn(Credentials.of("charlie", "newPass")))
                .doesNotThrowAnyException();
    }

    @Test void reset_password_flow() {
        Wiser wiser = new Wiser();
        wiser.setPort(2500);
        wiser.start();

        userService.createUser(adminHeader, new UserData.Builder()
                .username("delta")
                .password("pass")
                .email("delta@domain.com")
                .build());

        userService.requestToResetUserPassword("delta@domain.com");

        assertThatCode(() -> sessionService.logIn(Credentials.of("delta", "pass")))
                .doesNotThrowAnyException();

        String email = readEmail(wiser, 0);
        String emailCode = extractEmailCode(email);

        userService.resetUserPassword(PasswordResetData.of(emailCode, "newPass"));

        readEmail(wiser, 1);

        userService.requestToResetUserPassword("delta@domain.com");
        String email2 = readEmail(wiser, 2);
        String emailCode2 = extractEmailCode(email2);
        assertThat(emailCode2).isNotEqualTo(emailCode);

        wiser.stop();
    }

    @Test void get_user_by_jids_or_usernames() {
        User user1 = userService.createUser(adminHeader, new UserData.Builder()
                .username("gama")
                .password("pass")
                .email("alpha@domain.com")
                .build());

        User user2 = userService.createUser(adminHeader, new UserData.Builder()
                .username("goma")
                .password("pass")
                .email("goma@domain.com")
                .build());

        Set<String> jids = ImmutableSet.of(user1.getJid(), user2.getJid());
        Map<String, User> usersByJids = userService.findUsersByJids(jids);
        assertThat(usersByJids).containsOnly(
                new SimpleEntry<>(user1.getJid(), user1),
                new SimpleEntry<>(user2.getJid(), user2));

        Set<String> usernames = ImmutableSet.of(user1.getUsername(), user2.getUsername());
        Map<String, User> usersByUsernames = userService.findUsersByUsernames(usernames);
        assertThat(usersByUsernames).containsOnly(
                new SimpleEntry<>(user1.getUsername(), user1),
                new SimpleEntry<>(user2.getUsername(), user2));

        // must ignore not found jids
        jids = ImmutableSet.of(user1.getJid(), "88888");
        usersByJids = userService.findUsersByJids(jids);
        assertThat(usersByJids).containsExactly(new SimpleEntry<>(user1.getJid(), user1));

        // must ignore not found usernames
        usernames = ImmutableSet.of(user1.getUsername(), "88888");
        usersByUsernames = userService.findUsersByUsernames(usernames);
        assertThat(usersByUsernames).containsExactly(new SimpleEntry<>(user1.getUsername(), user1));
    }

    private static String readEmail(Wiser wiser, int index) {
        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> wiser.getMessages().size() > index);

        return new String(wiser.getMessages().get(index).getData());
    }

    private static String extractEmailCode(String email) {
        Pattern pattern = Pattern.compile("^.*#(\\w*)#.*$", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(email);
        matcher.matches();
        return matcher.group(1);
    }
}
