package judgels.api.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableEmailTemplate.class)
public interface EmailTemplate {
    String getSubject();
    String getBody();

    static EmailTemplate of(String subject, String body) {
        return new Builder().subject(subject).body(body).build();
    }

    class Builder extends ImmutableEmailTemplate.Builder {}
}
