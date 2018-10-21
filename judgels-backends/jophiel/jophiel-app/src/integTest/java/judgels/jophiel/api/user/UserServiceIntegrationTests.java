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
    void end_to_end_flow() {
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

        Set<String> usernames = ImmutableSet.of("nano", "budi");
        Map<String, String> jidsByUsernames = userService.translateUsernamesToJids(usernames);
        assertThat(jidsByUsernames).containsOnly(
                new SimpleEntry<>("nano", nano.getJid()),
                new SimpleEntry<>("budi", budi.getJid()));

        // must ignore not found usernames
        usernames = ImmutableSet.of("nano", "88888");
        jidsByUsernames = userService.translateUsernamesToJids(usernames);
        assertThat(jidsByUsernames).containsExactly(new SimpleEntry<>("nano", nano.getJid()));
    }
}
