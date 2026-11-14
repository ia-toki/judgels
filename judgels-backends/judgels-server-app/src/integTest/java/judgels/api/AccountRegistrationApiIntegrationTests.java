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
import judgels.api.setting.TrainingAccountRegistrationSettings;
import judgels.api.training.account.registration.AccountRegistrationData;
import judgels.client.AccountRegistrationClient;
import judgels.client.SessionClient;
import judgels.client.SettingClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.subethamail.wiser.Wiser;

class AccountRegistrationApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final AccountRegistrationClient registrationClient = createClient(AccountRegistrationClient.class);
    private final SessionClient sessionClient = createClient(SessionClient.class);
    private final SettingClient settingClient = createClient(SettingClient.class);

    @BeforeEach
    void enableRegistration() {
        setRegistrationEnabled(true);
    }

    @Test
    void registration_is_not_found_when_disabled() {
        setRegistrationEnabled(false);

        assertNotFound(() -> registrationClient.registerUser(new AccountRegistrationData.Builder()
                .username("gamma")
                .name("Gamma")
                .password("pass")
                .email("gamma@domain.com")
                .build()));
        assertNotFound(() -> registrationClient.activateUser("code"));
        assertNotFound(() -> registrationClient.resendActivationEmail("gamma@domain.com"));
    }

    @Test
    void register_activate_user() {
        Wiser wiser = new Wiser();
        wiser.setPort(9250);
        wiser.start();

        registrationClient.registerUser(new AccountRegistrationData.Builder()
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

        registrationClient.activateUser(emailCode);

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
        assertNotFound(() -> registrationClient.resendActivationEmail("nonexistent"));

        registrationClient.registerUser(new AccountRegistrationData.Builder()
                .username("alfa")
                .name("Alfa")
                .password("pass")
                .email("alfa@domain.com")
                .build());

        String email = readEmail(wiser, 0);
        String emailCode1 = extractEmailCode(email);

        // resend activation email
        assertPermitted(() -> registrationClient.resendActivationEmail("alfa@domain.com"));

        email = readEmail(wiser, 1);
        String emailCode2 = extractEmailCode(email);
        assertThat(emailCode2).isEqualTo(emailCode1);

        // log in after activation
        assertPermitted(() -> registrationClient.activateUser(emailCode2));

        // resend activation email with expired code
        assertNotFound(() -> registrationClient.resendActivationEmail("alfa@domain.com"));

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
