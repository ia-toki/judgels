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
    void get_user_by_jids_or_usernames() {
        User user1 = userService.createUser(adminHeader, new UserData.Builder()
                .username("gama")
                .password("pass")
                .email("alpha@domain.com")
                .build());
        UserInfo userInfo1 = new UserInfo.Builder()
                .username(user1.getUsername())
                .build();

        User user2 = userService.createUser(adminHeader, new UserData.Builder()
                .username("goma")
                .password("pass")
                .email("goma@domain.com")
                .build());
        UserInfo userInfo2 = new UserInfo.Builder()
                .username(user2.getUsername())
                .build();

        Set<String> jids = ImmutableSet.of(user1.getJid(), user2.getJid());
        Map<String, UserInfo> usersByJids = userService.findUsersByJids(jids);
        assertThat(usersByJids).containsOnly(
                new SimpleEntry<>(user1.getJid(), userInfo1),
                new SimpleEntry<>(user2.getJid(), userInfo2));

        Set<String> usernames = ImmutableSet.of(user1.getUsername(), user2.getUsername());
        Map<String, User> usersByUsernames = userService.findUsersByUsernames(usernames);
        assertThat(usersByUsernames).containsOnly(
                new SimpleEntry<>(user1.getUsername(), user1),
                new SimpleEntry<>(user2.getUsername(), user2));

        // must ignore not found jids
        jids = ImmutableSet.of(user1.getJid(), "88888");
        usersByJids = userService.findUsersByJids(jids);
        assertThat(usersByJids).containsExactly(new SimpleEntry<>(user1.getJid(), userInfo1));

        // must ignore not found usernames
        usernames = ImmutableSet.of(user1.getUsername(), "88888");
        usersByUsernames = userService.findUsersByUsernames(usernames);
        assertThat(usersByUsernames).containsExactly(new SimpleEntry<>(user1.getUsername(), user1));
    }
}
