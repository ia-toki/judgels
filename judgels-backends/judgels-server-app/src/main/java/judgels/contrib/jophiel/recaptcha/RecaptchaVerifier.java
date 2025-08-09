package judgels.contrib.jophiel.recaptcha;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.GenericType;
import java.util.HashMap;
import java.util.Map;
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
