package judgels.jophiel.api.user;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.session.SessionService;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.api.user.info.UserInfoService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.persistence.api.Page;
import judgels.service.api.actor.AuthHeader;
import org.junit.jupiter.api.Test;

class UserServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService userService = createService(UserService.class);
    private UserInfoService userInfoService = createService(UserInfoService.class);
    private UserSearchService userSearchService = createService(UserSearchService.class);
    private SessionService sessionService = createService(SessionService.class);

    @Test
    void end_to_end_flow() {
        User nano = userService.createUser(adminHeader, new UserData.Builder()
                .username("nano")
                .password("pass")
                .email("nano@domain.com")
                .build());
        User nani = userService.createUser(adminHeader, new UserData.Builder()
                .username("nani")
                .password("pass")
                .email("nani@domain.com")
                .build());

        Page<User> users = userService.getUsers(adminHeader, empty(), empty(), empty());
        assertThat(users.getPage()).contains(nani, nano);
    }

    @Test
    void admin_logout_user() {
        User userToLogout = userService.createUser(adminHeader, new UserData.Builder()
                .username("user_to_logout")
                .password("pass")
                .email("user_to_logout@domain.com")
                .build());

        Session session = sessionService.logIn(Credentials.of("user_to_logout", "pass"));
        assertThat(session.getUserJid()).isEqualTo(userToLogout.getJid());

        userService.logoutUser(adminHeader, userToLogout.getJid());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> userService.getUser(AuthHeader.of(session.getToken()), userToLogout.getJid()))
                .withMessageContaining("Judgels:Unauthorized");
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

    @Test
    void user_batch_upsert() throws IOException {
        UsersUpsertResponse response = userService.upsertUsers(adminHeader, "country,name,email,username,password\n"
                + "ID,Andi Indo,andi@judgels.com,andi,123\r\n"
                + "TH,Budi Thai,budi@judgels.com,budi,456\n");
        assertThat(response.getCreatedUsernames()).containsExactly("andi", "budi");
        assertThat(response.getUpdatedUsernames()).isEmpty();

        response = userService.upsertUsers(adminHeader, "country,name,email,username,password\r\n"
                + "TH,Budi Thai 2,budi2@judgels.com,budi,333\n"
                + "MY,Caca Malay,caca@judgels.com,caca,777\n");
        assertThat(response.getCreatedUsernames()).containsExactly("caca");
        assertThat(response.getUpdatedUsernames()).containsExactly("budi");

        Map<String, String> usernameToJid =
                userSearchService.translateUsernamesToJids(ImmutableSet.of("andi", "budi", "caca"));

        User andi = userService.getUser(adminHeader, usernameToJid.get("andi"));
        assertThat(andi.getEmail()).isEqualTo("andi@judgels.com");
        UserInfo andiInfo = userInfoService.getInfo(adminHeader, andi.getJid());
        assertThat(andiInfo.getCountry()).contains("ID");
        assertThat(andiInfo.getName()).contains("Andi Indo");

        User budi = userService.getUser(adminHeader, usernameToJid.get("budi"));
        assertThat(budi.getEmail()).isEqualTo("budi2@judgels.com");
        UserInfo budiInfo = userInfoService.getInfo(adminHeader, budi.getJid());
        assertThat(budiInfo.getCountry()).contains("TH");
        assertThat(budiInfo.getName()).contains("Budi Thai 2");

        User caca = userService.getUser(adminHeader, usernameToJid.get("caca"));
        assertThat(caca.getEmail()).isEqualTo("caca@judgels.com");
        UserInfo cacaInfo = userInfoService.getInfo(adminHeader, caca.getJid());
        assertThat(cacaInfo.getCountry()).contains("MY");
        assertThat(cacaInfo.getName()).contains("Caca Malay");
    }
}
