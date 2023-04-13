package judgels.jophiel.api.info;

import judgels.jophiel.api.AbstractServiceIntegrationTests;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.api.user.info.UserInfoService;
import judgels.service.api.actor.AuthHeader;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class UserInfoServicePermissionIntegrationTests extends AbstractServiceIntegrationTests {
    private final UserInfoService infoService = createService(UserInfoService.class);

    @Test
    void update_info() {
        User andi = createUser("andi");
        AuthHeader andiHeader = getHeader(andi);

        assertPermitted(updateInfo(adminHeader, andi.getJid()));
        assertPermitted(updateInfo(andiHeader, andi.getJid()));
        assertForbidden(updateInfo(userHeader, andi.getJid()));
    }

    @Test
    void get_info() {
        User budi = createUser("budi");
        AuthHeader budiHeader = getHeader(budi);

        assertPermitted(getInfo(adminHeader, budi.getJid()));
        assertPermitted(getInfo(budiHeader, budi.getJid()));
        assertForbidden(getInfo(userHeader, budi.getJid()));
    }

    private ThrowingCallable updateInfo(AuthHeader authHeader, String userJid) {
        return () -> infoService.updateInfo(authHeader, userJid, new UserInfo.Builder().build());
    }

    private ThrowingCallable getInfo(AuthHeader authHeader, String userJid) {
        return () -> infoService.getInfo(authHeader, userJid);
    }
}
