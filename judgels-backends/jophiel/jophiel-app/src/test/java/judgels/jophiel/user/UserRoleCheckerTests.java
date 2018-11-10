package judgels.jophiel.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import judgels.jophiel.role.AdminRoleStore;
import judgels.jophiel.role.SuperadminRoleStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class UserRoleCheckerTests {
    private static final String SUPERADMIN = "superadminJid";
    private static final String ADMIN = "adminJid";
    private static final String USER = "userJid";
    private static final String ANOTHER_USER = "anotherUserJid";

    @Mock private SuperadminRoleStore superadminRoleStore;
    @Mock private AdminRoleStore adminRoleStore;
    private UserRoleChecker checker;

    @BeforeEach
    void before() {
        initMocks(this);
        checker = new UserRoleChecker(superadminRoleStore, adminRoleStore);

        when(superadminRoleStore.isSuperadmin(SUPERADMIN)).thenReturn(true);
        when(adminRoleStore.isAdmin(ADMIN)).thenReturn(true);
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
