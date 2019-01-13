package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGabrielClientConfiguration.class)
public interface GabrielClientConfiguration {
    GabrielClientConfiguration DEFAULT = new Builder()
            .clientJid("xxx")
            .build();

    String getClientJid();

    class Builder extends ImmutableGabrielClientConfiguration.Builder {}
}
