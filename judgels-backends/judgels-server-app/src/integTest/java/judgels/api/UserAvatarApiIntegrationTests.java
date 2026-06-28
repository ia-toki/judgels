package judgels.api;

import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.assertj.core.api.Assertions.assertThat;

import feign.form.FormData;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.user.User;
import judgels.user.UserAvatarClient;
import org.junit.jupiter.api.Test;

class UserAvatarApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserAvatarClient avatarClient = createClient(UserAvatarClient.class);

    @Test
    void update_and_delete_avatar() {
        User andi = createUser("andi");
        String andiToken = getToken(andi);

        assertThat(avatarClient.avatarExists(andi.getJid())).isFalse();

        avatarClient.updateAvatar(andiToken, andi.getJid(),
                new FormData(MULTIPART_FORM_DATA, "avatar.png", new byte[]{1, 2, 3}));
        assertThat(avatarClient.avatarExists(andi.getJid())).isTrue();

        avatarClient.deleteAvatar(andiToken, andi.getJid());
        assertThat(avatarClient.avatarExists(andi.getJid())).isFalse();
    }
}
