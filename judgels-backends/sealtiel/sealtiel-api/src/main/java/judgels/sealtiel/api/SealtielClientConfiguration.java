package judgels.sealtiel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSealtielClientConfiguration.class)
public interface SealtielClientConfiguration {
    SealtielClientConfiguration DEFAULT = new Builder()
            .baseUrl("http://localhost:9003")
            .clientJid("JIDSECL-xxx")
            .clientSecret("xxx")
            .build();

    String getBaseUrl();
    String getClientJid();
    String getClientSecret();

    class Builder extends ImmutableSealtielClientConfiguration.Builder {}
}
