package judgels.jophiel.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.Set;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.jophiel.SessionClient;
import judgels.jophiel.UserClient;
import judgels.jophiel.UserInfoClient;
import judgels.jophiel.UserSearchClient;
import judgels.jophiel.api.session.Credentials;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UsersResponse;
import judgels.jophiel.api.user.UsersUpsertResponse;
import judgels.jophiel.api.user.info.UserInfo;
import org.junit.jupiter.api.Test;

class UserApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserClient userClient = createClient(UserClient.class);
    private final UserInfoClient userInfoClient = createClient(UserInfoClient.class);
    private final UserSearchClient userSearchClient = createClient(UserSearchClient.class);
    private final SessionClient sessionClient = createClient(SessionClient.class);

    @Test
    void create_get_export_users() {
        User nano = userClient.createUser(adminToken, new UserData.Builder()
                .username("nano")
                .password("pass")
                .email("nano@domain.com")
                .build());

        assertThat(nano.getUsername()).isEqualTo("nano");
        assertThat(nano.getEmail()).isEqualTo("nano@domain.com");

        assertNotFound(() -> userClient.getUser(adminToken, "bogus"));
        assertThat(userClient.getUser(adminToken, nano.getJid())).isEqualTo(nano);

        User nani = userClient.createUser(adminToken, new UserData.Builder()
                .username("nani")
                .password("pass")
                .email("nani@domain.com")
                .build());

        sessionClient.logIn(Credentials.of("nano", "pass")).getToken();

        UsersResponse response = userClient.getUsers(adminToken);
        assertThat(response.getData().getPage()).contains(nani, nano);
        assertThat(response.getLastSessionTimesMap()).containsKeys(nano.getJid());
        assertThat(response.getLastSessionTimesMap()).doesNotContainKeys(nani.getJid());

        String exportedCsv = userClient.exportUsers(adminToken, List.of("nani", "nano", "bogus"));
        assertThat(exportedCsv).isEqualTo(String.format("jid,username,email\n"
                + "%s,nani,nani@domain.com\n"
                + "%s,nano,nano@domain.com\n", nani.getJid(), nano.getJid()));
    }

    @Test
    void create_user__bad_request() {
        userClient.createUser(adminToken, new UserData.Builder()
                .username("xaxa")
                .password("pass")
                .email("xaxa@domain.com")
                .build());

        // duplicate username
        assertThatThrownBy(() -> userClient.createUser(adminToken, new UserData.Builder()
                .username("xaxa")
                .password("pass")
                .email("other@domain2.com")
                .build()))
                .hasFieldOrPropertyWithValue("code", 500); // TODO(fushar): should be 400

        // duplicate email
        assertThatThrownBy(() -> userClient.createUser(adminToken, new UserData.Builder()
                .username("xixi")
                .password("pass")
                .email("xaxa@domain.com")
                .build()))
                .hasFieldOrPropertyWithValue("code", 500); // TODO(fushar): should be 400
    }

    @Test
    void upsert_users() {
        // create
        UsersUpsertResponse response = userClient.upsertUsers(adminToken, "country,name,email,username,password\n"
                + "ID,Andi Indo,andi@judgels.com,andi,123\r\n"
                + "TH,Budi Thai,budi@judgels.com,budi,456\n");
        assertThat(response.getCreatedUsernames()).containsExactly("andi", "budi");
        assertThat(response.getUpdatedUsernames()).isEmpty();

        // update + create
        response = userClient.upsertUsers(adminToken, "country,name,email,username,password\r\n"
                + "TH,Budi Thai 2,budi2@judgels.com,budi,333\n"
                + "MY,Caca Malay,caca@judgels.com,caca,777\n");
        assertThat(response.getCreatedUsernames()).containsExactly("caca");
        assertThat(response.getUpdatedUsernames()).containsExactly("budi");

        // update only password
        response = userClient.upsertUsers(adminToken, "username,password\r\n"
                + "caca,778\n");
        assertThat(response.getUpdatedUsernames()).containsExactly("caca");

        // create with fixed jid
        response = userClient.upsertUsers(adminToken, "jid,email,username,password\r\n"
                + "JID123,dudi@judgels.com,dudi,888\n");
        assertThat(response.getCreatedUsernames()).containsExactly("dudi");

        // update without password
        response = userClient.upsertUsers(adminToken, "username,email\r\n"
                + "dudi,dudidudi@judgels.com\n");
        assertThat(response.getUpdatedUsernames()).containsExactly("dudi");

        Map<String, String> usernameToJid =
                userSearchClient.translateUsernamesToJids(Set.of("andi", "budi", "caca", "dudi"));

        User andi = userClient.getUser(adminToken, usernameToJid.get("andi"));
        assertThat(andi.getEmail()).isEqualTo("andi@judgels.com");
        UserInfo andiInfo = userInfoClient.getInfo(adminToken, andi.getJid());
        assertThat(andiInfo.getCountry()).contains("ID");
        assertThat(andiInfo.getName()).contains("Andi Indo");
        Session andiSession = sessionClient.logIn(Credentials.of("andi", "123"));

        User budi = userClient.getUser(adminToken, usernameToJid.get("budi"));
        assertThat(budi.getEmail()).isEqualTo("budi2@judgels.com");
        UserInfo budiInfo = userInfoClient.getInfo(adminToken, budi.getJid());
        assertThat(budiInfo.getCountry()).contains("TH");
        assertThat(budiInfo.getName()).contains("Budi Thai 2");
        Session budiSession = sessionClient.logIn(Credentials.of("budi", "333"));

        User caca = userClient.getUser(adminToken, usernameToJid.get("caca"));
        assertThat(caca.getEmail()).isEqualTo("caca@judgels.com");
        UserInfo cacaInfo = userInfoClient.getInfo(adminToken, caca.getJid());
        assertThat(cacaInfo.getCountry()).contains("MY");
        assertThat(cacaInfo.getName()).contains("Caca Malay");
        Session cacaSession = sessionClient.logIn(Credentials.of("caca", "778"));

        User dudi = userClient.getUser(adminToken, usernameToJid.get("dudi"));
        assertThat(dudi.getJid()).isEqualTo("JID123");
        assertThat(dudi.getEmail()).isEqualTo("dudidudi@judgels.com");
        Session dudiSession = sessionClient.logIn(Credentials.of("dudi", "888"));

        // update non-passwords, still logged in
        response = userClient.upsertUsers(adminToken, "username,country\r\n"
                + "andi,SG\n"
                + "budi,SG\n"
                + "caca,SG\n"
                + "dudi,SG\n");
        assertThat(response.getUpdatedUsernames()).containsExactly("andi", "budi", "caca", "dudi");

        assertPermitted(() -> userClient.getUser(andiSession.getToken(), andi.getJid()));
        assertPermitted(() -> userClient.getUser(budiSession.getToken(), budi.getJid()));
        assertPermitted(() -> userClient.getUser(cacaSession.getToken(), caca.getJid()));
        assertPermitted(() -> userClient.getUser(dudiSession.getToken(), dudi.getJid()));

        // update passwords, logged out
        response = userClient.upsertUsers(adminToken, "username,password\r\n"
                + "andi,111\n"
                + "budi,222\n"
                + "caca,333\n"
                + "dudi,444\n");
        assertThat(response.getUpdatedUsernames()).containsExactly("andi", "budi", "caca", "dudi");

        assertUnauthorized(() -> userClient.getUser(andiSession.getToken(), andi.getJid()));
        assertUnauthorized(() -> userClient.getUser(budiSession.getToken(), budi.getJid()));
        assertUnauthorized(() -> userClient.getUser(cacaSession.getToken(), caca.getJid()));
        assertUnauthorized(() -> userClient.getUser(dudiSession.getToken(), dudi.getJid()));
    }

    @Test
    void get_myself() {
        assertThat(userClient.getMyself(adminToken).getUsername()).isEqualTo("superadmin");
    }
}
