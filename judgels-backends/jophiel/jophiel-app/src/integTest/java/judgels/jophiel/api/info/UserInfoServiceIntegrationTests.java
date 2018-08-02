package judgels.jophiel.api.info;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.api.user.info.UserInfoService;
import org.junit.jupiter.api.Test;

class UserInfoServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private UserService userService = createService(UserService.class);
    private UserInfoService infoService = createService(UserInfoService.class);

    @Test
    void basic_flow() {
        User user = userService.createUser(adminHeader, new UserData.Builder()
                .username("alpha")
                .password("pass")
                .email("email@domain.com")
                .build());

        UserInfo info = new UserInfo.Builder()
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
    }
}
