package judgels.jophiel.user.master;

import static judgels.jophiel.user.master.MasterUsersCreator.DEFAULT_PASSWORD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.user.UserStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class MasterUsersCreatorTests {
    private static final String USER1 = "user1";
    private static final String USER2 = "user2";
    private static final String USER3 = "user3";

    @Mock private UserStore userStore;

    private MasterUsersCreator creator;

    @BeforeEach void before() {
        initMocks(this);

        creator = new MasterUsersCreator(userStore, ImmutableSet.of(USER1, USER2, USER3));

        when(userStore.findUserByUsername(USER2))
                .thenReturn(Optional.of(mock(User.class)));
    }

    @Test void creates_missing_users_and_skips_existing_users() {
        creator.create();

        verify(userStore).createUser(new UserData.Builder()
                .username(USER1)
                .password(DEFAULT_PASSWORD)
                .email("user1@jophiel.judgels")
                .name(USER1)
                .build());
        verify(userStore).createUser(new UserData.Builder()
                .username(USER3)
                .password(DEFAULT_PASSWORD)
                .email("user3@jophiel.judgels")
                .name(USER3)
                .build());

        verify(userStore, times(2)).createUser(any());
    }
}
