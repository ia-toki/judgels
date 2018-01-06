package judgels.recaptcha;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import org.glassfish.jersey.client.JerseyClientBuilder;

public class RecaptchaVerifier {
    private final String recaptchaSecretKey;

    public RecaptchaVerifier(String recaptchaSecretKey) {
        this.recaptchaSecretKey = recaptchaSecretKey;
    }

    public boolean verify(String response) {
        Client client = new JerseyClientBuilder().build();

        Form form = new Form();
        form.param("secret", recaptchaSecretKey);
        form.param("response", response);

        Map<String, Object> verification = client
                .target("https://www.google.com/recaptcha/api/siteverify")
                .request(APPLICATION_JSON)
                .post(Entity.form(form))
                .readEntity(new GenericType<HashMap<String, Object>>() {});

        return (boolean) verification.get("success");
    }
}
