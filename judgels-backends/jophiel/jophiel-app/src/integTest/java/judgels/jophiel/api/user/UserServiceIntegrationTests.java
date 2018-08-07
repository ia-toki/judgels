package judgels.jophiel.api.user;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Set;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.persistence.api.Page;
import org.junit.jupiter.api.Test;

class UserServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService userService = createService(UserService.class);

    @Test
    void basic_flow() {
        assertThat(userService.usernameExists("nano")).isFalse();
        assertThat(userService.emailExists("nano@domain.com")).isFalse();
        assertThat(userService.usernameExists("budi")).isFalse();
        assertThat(userService.emailExists("budi@domain.com")).isFalse();

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

        assertThat(userService.usernameExists("nano")).isTrue();
        assertThat(userService.emailExists("nano@domain.com")).isTrue();
        assertThat(userService.usernameExists("budi")).isTrue();
        assertThat(userService.emailExists("budi@domain.com")).isTrue();
        assertThat(userService.usernameExists("random")).isFalse();
        assertThat(userService.emailExists("random@random.com")).isFalse();

        Page<User> users = userService.getUsers(adminHeader, empty(), empty(), empty());
        assertThat(users.getData()).containsSequence(budi, nano);
    }

    @Test
    void translate_users_to_jids() {
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

        Set<String> usernames = ImmutableSet.of(user1.getUsername(), user2.getUsername());
        Map<String, String> jidsByUsernames = userService.translateUsernamesToJids(usernames);
        assertThat(jidsByUsernames).containsOnly(
                new SimpleEntry<>(user1.getUsername(), user1.getJid()),
                new SimpleEntry<>(user2.getUsername(), user2.getJid()));

        // must ignore not found usernames
        usernames = ImmutableSet.of(user1.getUsername(), "88888");
        jidsByUsernames = userService.translateUsernamesToJids(usernames);
        assertThat(jidsByUsernames).containsExactly(new SimpleEntry<>(user1.getUsername(), user1.getJid()));
    }
}
