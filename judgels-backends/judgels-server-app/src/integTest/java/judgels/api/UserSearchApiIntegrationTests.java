package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Set;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.user.User;
import judgels.client.UserSearchClient;
import org.junit.jupiter.api.Test;

class UserSearchApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserSearchClient searchClient = createClient(UserSearchClient.class);

    @Test
    void search_users() {
        assertThat(searchClient.hasUsername("nano")).isFalse();
        assertThat(searchClient.hasEmail("nano@domain.com")).isFalse();
        assertThat(searchClient.hasUsername("budi")).isFalse();
        assertThat(searchClient.hasEmail("budi@domain.com")).isFalse();

        User nano = createUser("nano");
        User budi = createUser("budi");

        assertThat(searchClient.hasUsername("nano")).isTrue();
        assertThat(searchClient.hasEmail("nano@domain.com")).isTrue();
        assertThat(searchClient.hasUsername("budi")).isTrue();
        assertThat(searchClient.hasEmail("budi@domain.com")).isTrue();
        assertThat(searchClient.hasUsername("random")).isFalse();
        assertThat(searchClient.hasEmail("random@random.com")).isFalse();

        Set<String> usernames = Set.of("nano", "budi", "88888");
        Map<String, String> jidsByUsernames = searchClient.translateUsernamesToJids(usernames);
        assertThat(jidsByUsernames).containsOnly(
                new SimpleEntry<>("nano", nano.getJid()),
                new SimpleEntry<>("budi", budi.getJid()));
    }
}
