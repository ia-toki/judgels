package judgels.jophiel.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.role.UserRole;
import judgels.jophiel.role.UserRoleStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class UserRoleCheckerTests {
    private static final String SUPERADMIN = "superadminJid";
    private static final String ADMIN = "adminJid";
    private static final String USER = "userJid";
    private static final String ANOTHER_USER = "anotherUserJid";

    @Mock private UserRoleStore userRoleStore;
    private UserRoleChecker checker;

    @BeforeEach
    void before() {
        initMocks(this);
        checker = new UserRoleChecker(userRoleStore);

        when(userRoleStore.getRole(SUPERADMIN))
                .thenReturn(new UserRole.Builder().jophiel(JophielRole.SUPERADMIN).build());
        when(userRoleStore.getRole(ADMIN))
                .thenReturn(new UserRole.Builder().jophiel(JophielRole.ADMIN).build());
        when(userRoleStore.getRole(USER))
                .thenReturn(new UserRole.Builder().jophiel(JophielRole.USER).build());
    }

    @Test
    void administer() {
        assertThat(checker.canAdminister(SUPERADMIN)).isTrue();
        assertThat(checker.canAdminister(ADMIN)).isTrue();
        assertThat(checker.canAdminister(USER)).isFalse();
    }

    @Test
    void manage() {
        assertThat(checker.canManage(SUPERADMIN, USER)).isTrue();
        assertThat(checker.canManage(ADMIN, USER)).isTrue();
        assertThat(checker.canManage(USER, USER)).isTrue();
        assertThat(checker.canManage(USER, ANOTHER_USER)).isFalse();
    }
}
