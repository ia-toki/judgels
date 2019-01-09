package judgels.gabriel.engines.batch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.engines.SingleSourceFileWithoutSubtasksGradingConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBatchGradingConfig.class)
public interface BatchGradingConfig extends SingleSourceFileWithoutSubtasksGradingConfig  {
    String getCustomScorer();

    class Builder extends ImmutableBatchGradingConfig.Builder {}
}
