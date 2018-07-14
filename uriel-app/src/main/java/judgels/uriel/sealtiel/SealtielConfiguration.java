package judgels.uriel.sealtiel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSealtielConfiguration.class)
public interface SealtielConfiguration {
    SealtielConfiguration DEFAULT = new Builder()
            .baseUrl("http://localhost:9003")
            .clientJid("xxx")
            .clientSecret("xxx")
            .build();

    String getBaseUrl();
    String getClientJid();
    String getClientSecret();

    class Builder extends ImmutableSealtielConfiguration.Builder {}
}
