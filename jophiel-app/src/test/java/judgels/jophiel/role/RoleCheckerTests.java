package judgels.jophiel.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class RoleCheckerTests {
    @Mock private RoleStore roleStore;
    private RoleChecker checker;

    @BeforeEach
    void before() {
        initMocks(this);
        checker = new RoleChecker(roleStore);

        when(roleStore.isAdmin("adminJid")).thenReturn(true);
    }

    @Test
    void create_user() {
        assertThat(checker.canCreateUser("adminJid")).isTrue();
        assertThat(checker.canCreateUser("userJid")).isFalse();
    }

    @Test
    void read_user() {
        assertThat(checker.canReadUser("adminJid", "userJid")).isTrue();
        assertThat(checker.canReadUser("userJid", "userJid")).isTrue();
        assertThat(checker.canReadUser("userJid", "anotherUserJid")).isFalse();
    }

    @Test
    void read_users() {
        assertThat(checker.canReadUsers("adminJid")).isTrue();
        assertThat(checker.canReadUsers("userJid")).isFalse();
        assertThat(checker.canReadUsers("anotherUserJid")).isFalse();
    }

    @Test
    void mutate_user() {
        assertThat(checker.canMutateUser("adminJid", "userJid")).isTrue();
        assertThat(checker.canMutateUser("userJid", "userJid")).isTrue();
        assertThat(checker.canMutateUser("userJid", "anotherUserJid")).isFalse();
    }
}
