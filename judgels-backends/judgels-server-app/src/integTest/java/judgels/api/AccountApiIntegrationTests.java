package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.session.Credentials;
import judgels.api.session.SessionWithRegistrationErrors;
import judgels.api.setting.SettingUpdateData;
import judgels.api.setting.TrainingAccountPasswordResetSettings;
import judgels.api.setting.TrainingAccountRegistrationSettings;
import judgels.api.training.account.AccountPasswordResetData;
import judgels.api.training.account.AccountRegistrationData;
import judgels.api.training.account.AccountRegistrationErrors;
import judgels.api.user.UserData;
import judgels.client.AccountClient;
import judgels.client.SessionClient;
import judgels.client.SettingClient;
import judgels.client.UserClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.subethamail.wiser.Wiser;

class AccountApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final AccountClient accountClient = createClient(AccountClient.class);
    private final SessionClient sessionClient = createClient(SessionClient.class);
    private final SettingClient settingClient = createClient(SettingClient.class);
    private final UserClient userClient = createClient(UserClient.class);

    @BeforeEach
    void enableRegistrationAndPasswordReset() {
        setRegistrationEnabled(true);
        setPasswordResetEnabled(true);
    }

    @Test
    void registration_is_not_found_when_disabled() {
        setRegistrationEnabled(false);

        assertNotFound(() -> accountClient.registerUser(new AccountRegistrationData.Builder()
                .username("gamma")
                .name("Gamma")
                .password("pass")
                .email("gamma@domain.com")
                .build()));
        assertNotFound(() -> accountClient.activateUser("code"));
        assertNotFound(() -> accountClient.resendActivationEmail("gamma@domain.com"));
    }

    @Test
    void registration_rejects_taken_username_or_email() {
        createUser("delta");

        assertBadRequest(() -> accountClient.registerUser(new AccountRegistrationData.Builder()
                .username("delta")
                .name("Delta")
                .password("pass")
                .email("epsilon@domain.com")
                .build()))
                .hasMessageContaining(AccountRegistrationErrors.USERNAME_ALREADY_EXISTS);

        assertBadRequest(() -> accountClient.registerUser(new AccountRegistrationData.Builder()
                .username("epsilon")
                .name("Delta")
                .password("pass")
                .email("delta@domain.com")
                .build()))
                .hasMessageContaining(AccountRegistrationErrors.EMAIL_ALREADY_EXISTS);
    }

    @Test
    void register_activate_user() {
        Wiser wiser = new Wiser();
        wiser.setPort(9250);
        wiser.start();

        accountClient.registerUser(new AccountRegistrationData.Builder()
                .username("beta")
                .name("Beta")
                .password("pass")
                .email("beta@domain.com")
                .build());
        Credentials credentials = Credentials.of("beta", "pass");

        // log in before activation
        assertForbidden(() -> sessionClient.logIn(credentials))
                .hasMessageContaining(SessionWithRegistrationErrors.USER_NOT_ACTIVATED);

        // read activation email
        String email = readEmail(wiser, 0);
        String emailCode = extractEmailCode(email);

        accountClient.activateUser(emailCode);

        // log in after activation
        assertPermitted(() -> sessionClient.logIn(credentials));

        wiser.stop();
    }

    @Test
    void resend_activation_email() {
        Wiser wiser = new Wiser();
        wiser.setPort(9250);
        wiser.start();

        // resend activation email with nonexistent code
        assertNotFound(() -> accountClient.resendActivationEmail("nonexistent"));

        accountClient.registerUser(new AccountRegistrationData.Builder()
                .username("alfa")
                .name("Alfa")
                .password("pass")
                .email("alfa@domain.com")
                .build());

        String email = readEmail(wiser, 0);
        String emailCode1 = extractEmailCode(email);

        // resend activation email
        assertPermitted(() -> accountClient.resendActivationEmail("alfa@domain.com"));

        email = readEmail(wiser, 1);
        String emailCode2 = extractEmailCode(email);
        assertThat(emailCode2).isEqualTo(emailCode1);

        // log in after activation
        assertPermitted(() -> accountClient.activateUser(emailCode2));

        // resend activation email with expired code
        assertNotFound(() -> accountClient.resendActivationEmail("alfa@domain.com"));

        wiser.stop();
    }

    @Test
    void password_reset_is_not_found_when_disabled() {
        setPasswordResetEnabled(false);

        assertNotFound(() -> accountClient.requestResetPassword("gamma@domain.com"));
        assertNotFound(() -> accountClient.resetPassword(AccountPasswordResetData.of("code", "newPass")));
    }

    @Test
    void reset_password() {
        Wiser wiser = new Wiser();
        wiser.setPort(9250);
        wiser.start();

        // allow requesting to reset random email in order not to leak info
        assertPermitted(() -> accountClient.requestResetPassword("zeta@domain.com"));

        userClient.createUser(adminToken, new UserData.Builder()
                .username("zeta")
                .password("pass")
                .email("zeta@domain.com")
                .build());

        accountClient.requestResetPassword("zeta@domain.com");

        // can still log in if password is not actually reset
        assertPermitted(() -> sessionClient.logIn(Credentials.of("zeta", "pass")));

        String email = readEmail(wiser, 0);
        String emailCode = extractEmailCode(email);

        // request to reset password again
        accountClient.requestResetPassword("zeta@domain.com");

        String email2 = readEmail(wiser, 1);
        String emailCode2 = extractEmailCode(email2);

        // assert that code is the same (not expired yet)
        assertThat(emailCode2).isEqualTo(emailCode);

        // reset the password
        accountClient.resetPassword(AccountPasswordResetData.of(emailCode, "newPass"));

        readEmail(wiser, 2);

        // request to reset password again
        accountClient.requestResetPassword("zeta@domain.com");

        String email3 = readEmail(wiser, 3);
        String emailCode3 = extractEmailCode(email3);

        // assert that previous code has expired and a new one is generated
        assertThat(emailCode3).isNotEqualTo(emailCode2);

        wiser.stop();
    }

    private void setRegistrationEnabled(boolean enabled) {
        settingClient.updateSettings(adminToken, new SettingUpdateData.Builder()
                .trainingAccountRegistration(new TrainingAccountRegistrationSettings.Builder()
                        .enabled(enabled)
                        .recaptchaEnabled(false)
                        .activationEmailTemplate(TrainingAccountRegistrationSettings.DEFAULT_ACTIVATION_EMAIL_TEMPLATE)
                        .build())
                .build());
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
                .pollInterval(50, TimeUnit.MILLISECONDS)
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
