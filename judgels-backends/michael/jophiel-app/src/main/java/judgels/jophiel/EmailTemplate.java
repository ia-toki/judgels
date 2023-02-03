package judgels.jophiel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableEmailTemplate.class)
public interface EmailTemplate {
    String getSubject();
    String getBody();

    class Builder extends ImmutableEmailTemplate.Builder {}
}
