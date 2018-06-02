package judgels.uriel.gabriel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGabrielConfiguration.class)
public interface GabrielConfiguration {
    GabrielConfiguration DEFAULT = new Builder()
            .clientJid("xxx")
            .build();

    String getClientJid();

    class Builder extends ImmutableGabrielConfiguration.Builder {}
}
