package judgels.uriel.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class RoleCheckerTests {
    private static final String ADMIN = "adminJid";
    private static final String USER = "userJid";
    private static final String CONTESTANT = "contestantJid";
    private static final String SUPERVISOR = "supervisorJid";
    private static final String MANAGER = "managerJid";
    private static final String CONTEST = "contestJid";

    @Mock private RoleStore roleStore;
    private RoleChecker checker;

    @BeforeEach
    void before() {
        initMocks(this);
        checker = new RoleChecker(roleStore);

        when(roleStore.isAdmin(ADMIN)).thenReturn(true);
        when(roleStore.isContestContestantOrAbove(CONTESTANT, CONTEST)).thenReturn(true);
        when(roleStore.isContestContestantOrAbove(SUPERVISOR, CONTEST)).thenReturn(true);
        when(roleStore.isContestSupervisorOrAbove(SUPERVISOR, CONTEST)).thenReturn(true);
        when(roleStore.isContestContestantOrAbove(MANAGER, CONTEST)).thenReturn(true);
        when(roleStore.isContestSupervisorOrAbove(MANAGER, CONTEST)).thenReturn(true);
        when(roleStore.isContestManager(MANAGER, CONTEST)).thenReturn(true);
    }

    @Test
    void read_contest() {
        assertThat(checker.canReadContest(ADMIN, CONTEST)).isTrue();
        assertThat(checker.canReadContest(USER, CONTEST)).isFalse();
        assertThat(checker.canReadContest(CONTESTANT, CONTEST)).isTrue();
        assertThat(checker.canReadContest(SUPERVISOR, CONTEST)).isTrue();
        assertThat(checker.canReadContest(MANAGER, CONTEST)).isTrue();
    }

    @Test
    void create_contest() {
        assertThat(checker.canCreateContest(ADMIN)).isTrue();
        assertThat(checker.canCreateContest(USER)).isFalse();
    }
}
