package judgels.jophiel.api.user.search;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Set;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import org.junit.jupiter.api.Test;

class UserSearchServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService userService = createService(UserService.class);
    private UserSearchService searchService = createService(UserSearchService.class);

    @Test
    void end_to_end_flow() {
        assertThat(searchService.usernameExists("nano")).isFalse();
        assertThat(searchService.emailExists("nano@domain.com")).isFalse();
        assertThat(searchService.usernameExists("budi")).isFalse();
        assertThat(searchService.emailExists("budi@domain.com")).isFalse();

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

        assertThat(searchService.usernameExists("nano")).isTrue();
        assertThat(searchService.emailExists("nano@domain.com")).isTrue();
        assertThat(searchService.usernameExists("budi")).isTrue();
        assertThat(searchService.emailExists("budi@domain.com")).isTrue();
        assertThat(searchService.usernameExists("random")).isFalse();
        assertThat(searchService.emailExists("random@random.com")).isFalse();

        Set<String> usernames = ImmutableSet.of("nano", "budi");
        Map<String, String> jidsByUsernames = searchService.translateUsernamesToJids(usernames);
        assertThat(jidsByUsernames).containsOnly(
                new SimpleEntry<>("nano", nano.getJid()),
                new SimpleEntry<>("budi", budi.getJid()));

        // must ignore not found usernames
        usernames = ImmutableSet.of("nano", "88888");
        jidsByUsernames = searchService.translateUsernamesToJids(usernames);
        assertThat(jidsByUsernames).containsExactly(new SimpleEntry<>("nano", nano.getJid()));
    }
}
