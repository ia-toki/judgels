package judgels.gabriel.engines.functional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.engines.MultipleSourceFilesWithoutSubtasksGradingConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableFunctionalGradingConfig.class)
public interface FunctionalGradingConfig extends MultipleSourceFilesWithoutSubtasksGradingConfig {
    @JsonInclude(Include.NON_ABSENT)
    Optional<String> getCustomScorer();

    class Builder extends ImmutableFunctionalGradingConfig.Builder {}
}
