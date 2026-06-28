package judgels.api;

import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

import feign.form.FormData;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.user.User;
import judgels.user.UserAvatarClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class UserAvatarApiPermissionIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserAvatarClient avatarClient = createClient(UserAvatarClient.class);

    @Test
    void update_avatar() {
        User andi = createUser("andi");
        String andiToken = getToken(andi);

        assertPermitted(updateAvatar(andiToken, andi.getJid()));
        assertPermitted(updateAvatar(adminToken, andi.getJid()));
        assertForbidden(updateAvatar(userToken, andi.getJid()));
    }

    @Test
    void delete_avatar() {
        User budi = createUser("budi");
        String budiToken = getToken(budi);

        assertPermitted(deleteAvatar(budiToken, budi.getJid()));
        assertPermitted(deleteAvatar(adminToken, budi.getJid()));
        assertForbidden(deleteAvatar(userToken, budi.getJid()));
    }

    private ThrowingCallable updateAvatar(String token, String userJid) {
        return () -> avatarClient.updateAvatar(token, userJid,
                new FormData(MULTIPART_FORM_DATA, "avatar.png", new byte[]{1, 2, 3}));
    }

    private ThrowingCallable deleteAvatar(String token, String userJid) {
        return () -> avatarClient.deleteAvatar(token, userJid);
    }
}
