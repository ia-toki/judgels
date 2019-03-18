package judgels.gabriel.engines.functional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.engines.MultipleSourceFilesWithSubtasksGradingConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableFunctionalWithSubtasksGradingConfig.class)
public interface FunctionalWithSubtasksGradingConfig extends MultipleSourceFilesWithSubtasksGradingConfig {
    @JsonInclude(Include.NON_ABSENT)
    Optional<String> getCustomScorer();

    class Builder extends ImmutableFunctionalWithSubtasksGradingConfig.Builder {}
}
