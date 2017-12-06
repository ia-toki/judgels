package judgels.jophiel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.jophiel.mailer.MailerConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJophielConfiguration.class)
public interface JophielConfiguration {
    JophielConfiguration DEFAULT = ImmutableJophielConfiguration.builder().build();

    @JsonProperty("mailer")
    Optional<MailerConfiguration> getMailerConfig();
}
