package judgels.sandalphon.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSandalphonClientConfiguration.class)
public interface SandalphonClientConfiguration {
    SandalphonClientConfiguration DEFAULT = new Builder()
            .baseUrl("http://localhost:9002")
            .clientJid("JIDSACL-xxx")
            .clientSecret("xxx")
            .build();

    String getBaseUrl();
    String getClientJid();
    String getClientSecret();

    class Builder extends ImmutableSandalphonClientConfiguration.Builder {}
}
