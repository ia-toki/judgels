package judgels.jophiel.api.user;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
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

    @Test
    void user_batch_create() {
        List<UserData> data = ImmutableList.of(
                new UserData.Builder()
                        .username("dina")
                        .password("pass")
                        .email("dina@domain.com")
                        .build(),
                new UserData.Builder()
                        .username("dino")
                        .password("pass")
                        .email("dino@domain.com")
                        .build()
        );
        List<User> users = userService.createUsers(adminHeader, data);

        assertThat(users.size()).isEqualTo(2);

        User dina = users.get(0);
        User dino = users.get(1);

        assertThat(dina.getUsername()).isEqualTo("dina");
        assertThat(dino.getUsername()).isEqualTo("dino");

        Page<User> usersPage = userService.getUsers(adminHeader, empty(), empty(), empty());
        assertThat(usersPage.getPage()).contains(dina, dino);
    }
}
