package judgels.jophiel.api.info;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.api.user.info.UserInfoService;
import org.junit.jupiter.api.Test;

class UserInfoServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private final UserInfoService infoService = createService(UserInfoService.class);

    @Test
    void update_info() {
        UserInfo info = new UserInfo.Builder().build();
        infoService.updateInfo(adminHeader, user.getJid(), info);
        assertThat(infoService.getInfo(adminHeader, user.getJid())).isEqualTo(info);

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
        infoService.updateInfo(adminHeader, user.getJid(), info);
        assertThat(infoService.getInfo(adminHeader, user.getJid())).isEqualTo(info);

        info = new UserInfo.Builder().build();
        infoService.updateInfo(adminHeader, user.getJid(), info);
        assertThat(infoService.getInfo(adminHeader, user.getJid())).isEqualTo(info);
    }
}
