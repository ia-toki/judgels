package judgels.jophiel.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Set;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.jophiel.UserSearchClient;
import judgels.jophiel.api.user.User;
import org.junit.jupiter.api.Test;

class UserSearchApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserSearchClient searchClient = createClient(UserSearchClient.class);

    @Test
    void search_users() {
        assertThat(searchClient.usernameExists("nano")).isFalse();
        assertThat(searchClient.emailExists("nano@domain.com")).isFalse();
        assertThat(searchClient.usernameExists("budi")).isFalse();
        assertThat(searchClient.emailExists("budi@domain.com")).isFalse();

        User nano = createUser("nano");
        User budi = createUser("budi");

        assertThat(searchClient.usernameExists("nano")).isTrue();
        assertThat(searchClient.emailExists("nano@domain.com")).isTrue();
        assertThat(searchClient.usernameExists("budi")).isTrue();
        assertThat(searchClient.emailExists("budi@domain.com")).isTrue();
        assertThat(searchClient.usernameExists("random")).isFalse();
        assertThat(searchClient.emailExists("random@random.com")).isFalse();

        Set<String> usernames = Set.of("nano", "budi", "88888");
        Map<String, String> jidsByUsernames = searchClient.translateUsernamesToJids(usernames);
        assertThat(jidsByUsernames).containsOnly(
                new SimpleEntry<>("nano", nano.getJid()),
                new SimpleEntry<>("budi", budi.getJid()));
    }
}
