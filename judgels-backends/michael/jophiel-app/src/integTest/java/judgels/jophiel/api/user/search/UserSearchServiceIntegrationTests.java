package judgels.jophiel.api.user.search;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Set;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.user.User;
import org.junit.jupiter.api.Test;

class UserSearchServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private final UserSearchService searchService = createService(UserSearchService.class);

    @Test
    void search_users() {
        assertThat(searchService.usernameExists("nano")).isFalse();
        assertThat(searchService.emailExists("nano@domain.com")).isFalse();
        assertThat(searchService.usernameExists("budi")).isFalse();
        assertThat(searchService.emailExists("budi@domain.com")).isFalse();

        User nano = createUser("nano");
        User budi = createUser("budi");

        assertThat(searchService.usernameExists("nano")).isTrue();
        assertThat(searchService.emailExists("nano@domain.com")).isTrue();
        assertThat(searchService.usernameExists("budi")).isTrue();
        assertThat(searchService.emailExists("budi@domain.com")).isTrue();
        assertThat(searchService.usernameExists("random")).isFalse();
        assertThat(searchService.emailExists("random@random.com")).isFalse();

        Set<String> usernames = ImmutableSet.of("nano", "budi", "88888");
        Map<String, String> jidsByUsernames = searchService.translateUsernamesToJids(usernames);
        assertThat(jidsByUsernames).containsOnly(
                new SimpleEntry<>("nano", nano.getJid()),
                new SimpleEntry<>("budi", budi.getJid()));
    }
}
