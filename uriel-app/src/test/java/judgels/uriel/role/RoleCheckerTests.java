package judgels.uriel.role;

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
    void read_contest() {
        when(roleStore.isContestant("contestantJid", "contestA")).thenReturn(true);

        assertThat(checker.canReadContest("adminJid", "contestA")).isTrue();
        assertThat(checker.canReadContest("contestantJid", "contestA")).isTrue();
        assertThat(checker.canReadContest("randomJid", "contestA")).isFalse();
    }

}
