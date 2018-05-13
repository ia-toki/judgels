package judgels.uriel.sandalphon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSandalphonConfiguration.class)
public interface SandalphonConfiguration {
    SandalphonConfiguration DEFAULT = new Builder()
            .baseUrl("http://localhost:9002")
            .clientJid("xxx")
            .clientSecret("xxx")
            .build();

    String getBaseUrl();
    String getClientJid();
    String getClientSecret();

    class Builder extends ImmutableSandalphonConfiguration.Builder {}
}
