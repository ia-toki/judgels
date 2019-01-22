package judgels.gabriel.engines.batch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.engines.SingleSourceFileWithoutSubtasksGradingConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBatchGradingConfig.class)
public interface BatchGradingConfig extends SingleSourceFileWithoutSubtasksGradingConfig  {
    Optional<String> getCustomScorer();

    class Builder extends ImmutableBatchGradingConfig.Builder {}
}
