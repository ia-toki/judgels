package tlx.jophiel.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import judgels.BaseJudgelsApiIntegrationTests;
import judgels.jophiel.SessionClient;
import judgels.jophiel.api.session.Credentials;
import org.junit.jupiter.api.Test;
import org.subethamail.wiser.Wiser;
import tlx.jophiel.TlxUserAccountClient;
import tlx.jophiel.api.session.TlxSessionErrors;
import tlx.jophiel.api.user.account.UserRegistrationData;

class TlxUserAccountApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final TlxUserAccountClient accountClient = createClient(TlxUserAccountClient.class);
    private final SessionClient sessionClient = createClient(SessionClient.class);

    @Test
    void register_activate_user() {
        Wiser wiser = new Wiser();
        wiser.setPort(9250);
        wiser.start();

        accountClient.registerUser(new UserRegistrationData.Builder()
                .username("beta")
                .name("Beta")
                .password("pass")
                .email("beta@domain.com")
                .build());
        Credentials credentials = Credentials.of("beta", "pass");

        // log in before activation
        assertForbidden(() -> sessionClient.logIn(credentials))
                .hasMessageContaining(TlxSessionErrors.USER_NOT_ACTIVATED);

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

        accountClient.registerUser(new UserRegistrationData.Builder()
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
