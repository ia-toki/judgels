package judgels.jophiel.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class RoleCheckerTests {
    private static final String SUPERADMIN = "superadminJid";
    private static final String ADMIN = "adminJid";
    private static final String USER = "userJid";
    private static final String ANOTHER_USER = "anotherUserJid";

    @Mock private SuperadminRoleStore superadminRoleStore;
    @Mock private AdminRoleStore adminRoleStore;
    private RoleChecker checker;

    @BeforeEach
    void before() {
        initMocks(this);
        checker = new RoleChecker(superadminRoleStore, adminRoleStore);

        when(superadminRoleStore.isSuperadmin(SUPERADMIN)).thenReturn(true);
        when(adminRoleStore.isAdmin(ADMIN)).thenReturn(true);
    }

    @Test
    void create_user() {
        assertThat(checker.canCreateUser(SUPERADMIN)).isTrue();
        assertThat(checker.canCreateUser(ADMIN)).isTrue();
        assertThat(checker.canCreateUser(USER)).isFalse();
    }

    @Test
    void view_user() {
        assertThat(checker.canViewUser(SUPERADMIN, USER)).isTrue();
        assertThat(checker.canViewUser(ADMIN, USER)).isTrue();
        assertThat(checker.canViewUser(USER, USER)).isTrue();
        assertThat(checker.canViewUser(USER, ANOTHER_USER)).isFalse();
    }

    @Test
    void view_user_list() {
        assertThat(checker.canViewUserList(SUPERADMIN)).isTrue();
        assertThat(checker.canViewUserList(ADMIN)).isTrue();
        assertThat(checker.canViewUserList(USER)).isFalse();
    }

    @Test
    void update_user() {
        assertThat(checker.canUpdateUser(SUPERADMIN, USER)).isTrue();
        assertThat(checker.canUpdateUser(ADMIN, USER)).isTrue();
        assertThat(checker.canUpdateUser(USER, USER)).isTrue();
        assertThat(checker.canUpdateUser(USER, ANOTHER_USER)).isFalse();
    }

    @Test
    void update_user_list() {
        assertThat(checker.canUpdateUserList(SUPERADMIN)).isTrue();
        assertThat(checker.canUpdateUserList(ADMIN)).isTrue();
        assertThat(checker.canUpdateUserList(USER)).isFalse();
    }
}
