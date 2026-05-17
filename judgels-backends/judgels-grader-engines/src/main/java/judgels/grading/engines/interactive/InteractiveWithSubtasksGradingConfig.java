package judgels.grading.engines.interactive;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.grading.engines.SingleSourceFileWithSubtasksGradingConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableInteractiveWithSubtasksGradingConfig.class)
public interface InteractiveWithSubtasksGradingConfig extends SingleSourceFileWithSubtasksGradingConfig {
    Optional<String> getCommunicator();

    class Builder extends ImmutableInteractiveWithSubtasksGradingConfig.Builder {}
}
