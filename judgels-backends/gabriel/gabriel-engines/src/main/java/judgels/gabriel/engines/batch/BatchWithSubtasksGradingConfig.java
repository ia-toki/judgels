package judgels.gabriel.engines.batch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.engines.SingleSourceFileWithSubtasksGradingConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBatchWithSubtasksGradingConfig.class)
public interface BatchWithSubtasksGradingConfig extends SingleSourceFileWithSubtasksGradingConfig  {
    Optional<String> getCustomScorer();

    class Builder extends ImmutableBatchWithSubtasksGradingConfig.Builder {}
}
