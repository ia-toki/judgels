package judgels.jophiel.user.superadmin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.user.UserStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class SuperadminCreatorTests {
    @Mock private UserStore userStore;

    private SuperadminCreator creator;

    @BeforeEach void before() {
        initMocks(this);

        creator = new SuperadminCreator(userStore);
    }

    @Test void skips_existing_superadmin() {
        when(userStore.findUserByUsername("superadmin"))
                .thenReturn(Optional.of(mock(User.class)));

        creator.create();

        verify(userStore, times(0)).createUser(any());
    }

    @Test void creates_missing_superadmin() {
        creator.create();

        verify(userStore).createUser(new UserData.Builder()
                .username("superadmin")
                .password("superadmin")
                .email("superadmin@jophiel.judgels")
                .build());
        verify(userStore, times(1)).createUser(any());
    }
}
