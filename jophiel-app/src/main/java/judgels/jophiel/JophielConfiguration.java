package judgels.jophiel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import java.util.Set;
import judgels.jophiel.mailer.MailerConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJophielConfiguration.class)
public interface JophielConfiguration {
    JophielConfiguration DEFAULT = new JophielConfiguration.Builder().build();

    Set<String> getMasterUsers();

    @JsonProperty("mailer")
    Optional<MailerConfiguration> getMailerConfig();

    class Builder extends ImmutableJophielConfiguration.Builder {}
}
