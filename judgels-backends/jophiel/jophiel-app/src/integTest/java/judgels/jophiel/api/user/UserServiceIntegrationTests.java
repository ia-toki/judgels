package judgels.jophiel.api.user;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.persistence.api.Page;
import org.junit.jupiter.api.Test;

class UserServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService userService = createService(UserService.class);

    @Test
    void end_to_end_flow() {
        User nano = userService.createUser(adminHeader, new UserData.Builder()
                .username("nano")
                .password("pass")
                .email("nano@domain.com")
                .build());
        User budi = userService.createUser(adminHeader, new UserData.Builder()
                .username("budi")
                .password("pass")
                .email("budi@domain.com")
                .build());

        Page<User> users = userService.getUsers(adminHeader, empty(), empty(), empty());
        assertThat(users.getPage()).contains(budi, nano);
    }
}
