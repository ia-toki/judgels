package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.session.Credentials;
import judgels.api.setting.SettingUpdateData;
import judgels.api.setting.TrainingAccountPasswordResetSettings;
import judgels.api.training.account.password.AccountPasswordResetData;
import judgels.api.user.UserData;
import judgels.client.AccountPasswordClient;
import judgels.client.SessionClient;
import judgels.client.SettingClient;
import judgels.client.UserClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.subethamail.wiser.Wiser;

class AccountPasswordApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final UserClient userClient = createClient(UserClient.class);
    private final AccountPasswordClient passwordClient = createClient(AccountPasswordClient.class);
    private final SessionClient sessionClient = createClient(SessionClient.class);
    private final SettingClient settingClient = createClient(SettingClient.class);

    @BeforeEach
    void enablePasswordReset() {
        setPasswordResetEnabled(true);
    }

    @Test
    void password_reset_is_not_found_when_disabled() {
        setPasswordResetEnabled(false);

        assertNotFound(() -> passwordClient.requestToResetPassword("gamma@domain.com"));
        assertNotFound(() -> passwordClient.resetPassword(AccountPasswordResetData.of("code", "newPass")));
    }

    @Test
    void reset_password() {
        Wiser wiser = new Wiser();
        wiser.setPort(9250);
        wiser.start();

        // allow requesting to reset random email in order not to leak info
        assertPermitted(() -> passwordClient.requestToResetPassword("delta@domain.com"));

        userClient.createUser(adminToken, new UserData.Builder()
                .username("delta")
                .password("pass")
                .email("delta@domain.com")
                .build());

        passwordClient.requestToResetPassword("delta@domain.com");

        // can still log in if password is not actually reset
        assertPermitted(() -> sessionClient.logIn(Credentials.of("delta", "pass")));

        String email = readEmail(wiser, 0);
        String emailCode = extractEmailCode(email);

        // request to reset password again
        passwordClient.requestToResetPassword("delta@domain.com");

        String email2 = readEmail(wiser, 1);
        String emailCode2 = extractEmailCode(email2);

        // assert that code is the same (not expired yet)
        assertThat(emailCode2).isEqualTo(emailCode);

        // reset the password
        passwordClient.resetPassword(AccountPasswordResetData.of(emailCode, "newPass"));

        readEmail(wiser, 2);

        // request to reset password again
        passwordClient.requestToResetPassword("delta@domain.com");

        String email3 = readEmail(wiser, 3);
        String emailCode3 = extractEmailCode(email3);

        // assert that previous code has expired and a new one is generated
        assertThat(emailCode3).isNotEqualTo(emailCode2);

        wiser.stop();
    }

    private void setPasswordResetEnabled(boolean enabled) {
        settingClient.updateSettings(adminToken, new SettingUpdateData.Builder()
                .trainingAccountPasswordReset(new TrainingAccountPasswordResetSettings.Builder()
                        .enabled(enabled)
                        .requestEmailTemplate(TrainingAccountPasswordResetSettings.DEFAULT_REQUEST_EMAIL_TEMPLATE)
                        .resetEmailTemplate(TrainingAccountPasswordResetSettings.DEFAULT_RESET_EMAIL_TEMPLATE)
                        .build())
                .build());
    }

    private static String readEmail(Wiser wiser, int index) {
        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> wiser.getMessages().size() > index);

        return new String(wiser.getMessages().get(index).getData());
    }

    private static String extractEmailCode(String email) {
        Pattern pattern = Pattern.compile("^.*#(\\w*)#.*$", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(email);
        matcher.matches();
        return matcher.group(1);
    }
}
