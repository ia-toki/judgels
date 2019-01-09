package judgels.gabriel.engines.interactive;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.engines.SingleSourceFileWithSubtasksGradingConfig;
import judgels.gabriel.engines.batch.ImmutableBatchGradingConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBatchGradingConfig.class)
public interface InteractiveWithSubtasksGradingConfig extends SingleSourceFileWithSubtasksGradingConfig {
    String getCommunicator();

    class Builder extends ImmutableInteractiveWithSubtasksGradingConfig.Builder {}
}
