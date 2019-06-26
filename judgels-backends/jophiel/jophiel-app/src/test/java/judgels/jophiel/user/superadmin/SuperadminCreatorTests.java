package judgels.jophiel.user.superadmin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.role.SuperadminRoleStore;
import judgels.jophiel.user.UserStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class SuperadminCreatorTests {
    @Mock private UserStore userStore;
    @Mock private SuperadminRoleStore superadminRoleStore;

    private SuperadminCreator creator;

    @BeforeEach
    void before() {
        initMocks(this);
    }

    @Test
    void skips_existing_superadmin() {
        when(userStore.getUserByUsername("superadmin"))
                .thenReturn(Optional.of(new User.Builder()
                        .jid("superadminUserJid")
                        .username("superadmin")
                        .email("superadmin@jophiel.judgels")
                        .build()));

        SuperadminCreatorConfiguration config = new SuperadminCreatorConfiguration.Builder()
                .enabled(true)
                .build();
        creator = new SuperadminCreator(userStore, superadminRoleStore, config);

        creator.createIfEnabled();

        verify(userStore, times(0)).createUser(any());
        verify(superadminRoleStore).setSuperadmin("superadminUserJid");
    }

    @Test
    void creates_missing_superadmin() {
        when(userStore.createUser(any())).thenReturn(new User.Builder()
                .jid("superadminUserJid")
                .username("superadmin")
                .email("superadmin@jophiel.judgels")
                .build());

        SuperadminCreatorConfiguration config = new SuperadminCreatorConfiguration.Builder()
                .enabled(true)
                .build();
        creator = new SuperadminCreator(userStore, superadminRoleStore, config);

        creator.createIfEnabled();

        verify(userStore).createUser(new UserData.Builder()
                .username("superadmin")
                .password("superadmin")
                .email("superadmin@jophiel.judgels")
                .build());
        verify(userStore, times(1)).createUser(any());
        verify(superadminRoleStore).setSuperadmin("superadminUserJid");
    }

    @Test
    void creates_missing_superadmin_with_custom_params() {
        when(userStore.createUser(any())).thenReturn(new User.Builder()
                .jid("superadminUserJid")
                .username("customusername")
                .email("customemail@customdomain.com")
                .build());

        SuperadminCreatorConfiguration config = new SuperadminCreatorConfiguration.Builder()
                .enabled(true)
                .username("customusername")
                .initialPassword("custompassword")
                .initialEmail("customemail@customdomain.com")
                .build();
        creator = new SuperadminCreator(userStore, superadminRoleStore, config);

        creator.createIfEnabled();

        verify(userStore).createUser(new UserData.Builder()
                .username("customusername")
                .password("custompassword")
                .email("customemail@customdomain.com")
                .build());
        verify(userStore, times(1)).createUser(any());
        verify(superadminRoleStore).setSuperadmin("superadminUserJid");
    }

    @Test
    void does_not_run_if_disabled() {
        when(userStore.createUser(any())).thenReturn(new User.Builder()
                .jid("superadminUserJid")
                .username("superadmin")
                .email("superadmin@jophiel.judgels")
                .build());

        SuperadminCreatorConfiguration config = new SuperadminCreatorConfiguration.Builder()
                .enabled(false)
                .build();
        creator = new SuperadminCreator(userStore, superadminRoleStore, config);

        creator.createIfEnabled();

        verify(userStore, times(0)).createUser(any());
        verify(superadminRoleStore, times(0)).setSuperadmin(any());
    }
}
