package judgels.jophiel.api;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.jophiel.UserInfoClient;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.info.UserInfo;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class UserInfoApiPermissionIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserInfoClient infoClient = createClient(UserInfoClient.class);

    @Test
    void update_info() {
        User andi = createUser("andi");
        String andiToken = getToken(andi);

        assertPermitted(updateInfo(adminToken, andi.getJid()));
        assertPermitted(updateInfo(andiToken, andi.getJid()));
        assertForbidden(updateInfo(userToken, andi.getJid()));
    }

    @Test
    void get_info() {
        User budi = createUser("budi");
        String budiToken = getToken(budi);

        assertPermitted(getInfo(adminToken, budi.getJid()));
        assertPermitted(getInfo(budiToken, budi.getJid()));
        assertForbidden(getInfo(userToken, budi.getJid()));
    }

    private ThrowingCallable updateInfo(String token, String userJid) {
        return () -> infoClient.updateInfo(token, userJid, new UserInfo.Builder().build());
    }

    private ThrowingCallable getInfo(String token, String userJid) {
        return () -> infoClient.getInfo(token, userJid);
    }
}
