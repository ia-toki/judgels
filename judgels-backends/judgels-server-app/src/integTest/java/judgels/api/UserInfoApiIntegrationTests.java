package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.user.User;
import judgels.api.user.info.UserInfo;
import judgels.user.UserInfoClient;
import org.junit.jupiter.api.Test;

class UserInfoApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserInfoClient infoClient = createClient(UserInfoClient.class);

    @Test
    void update_info() {
        User andi = createUser("andi");

        UserInfo info = new UserInfo.Builder().build();
        infoClient.updateInfo(adminToken, andi.getJid(), info);
        assertThat(infoClient.getInfo(adminToken, user.getJid())).isEqualTo(info);

        info = new UserInfo.Builder()
                .name("Alpha")
                .gender("MALE")
                .country("id")
                .homeAddress("address")
                .shirtSize("L")
                .institutionName("university")
                .institutionCountry("nation")
                .institutionProvince("province")
                .institutionCity("town")
                .build();
        infoClient.updateInfo(adminToken, user.getJid(), info);
        assertThat(infoClient.getInfo(adminToken, user.getJid())).isEqualTo(info);

        info = new UserInfo.Builder().build();
        infoClient.updateInfo(adminToken, user.getJid(), info);
        assertThat(infoClient.getInfo(adminToken, user.getJid())).isEqualTo(info);
    }
}
