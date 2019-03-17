package judgels.gabriel.engines.interactive;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.engines.SingleSourceFileWithoutSubtasksGradingConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableInteractiveGradingConfig.class)
public interface InteractiveGradingConfig extends SingleSourceFileWithoutSubtasksGradingConfig {
    Optional<String> getCommunicator();

    class Builder extends ImmutableInteractiveGradingConfig.Builder {}
}
