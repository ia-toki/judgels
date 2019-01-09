package judgels.gabriel.engines.functional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.engines.MultipleSourceFilesGradingConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableFunctionalWithSubtasksGradingConfig.class)
public interface FunctionalWithSubtasksGradingConfig extends MultipleSourceFilesGradingConfig {
    Optional<String> getCustomScorer();

    class Builder extends ImmutableFunctionalWithSubtasksGradingConfig.Builder {}
}
