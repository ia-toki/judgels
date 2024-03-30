package judgels.gabriel.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGradingOptions.class)
public interface GradingOptions {
    /**
     * Whether the input and solution output should be stored as part of the grading details, revealed to the contestant.
     * This is experimental. Currently, for this option to be honored, all the following must be satisfied:
     * - Grading engine is Batch.
     * - No sample test data.
     * - Official test data consists of at most 3 test cases.
     */
    @Value.Default
    @JsonInclude(Include.NON_DEFAULT)
    default boolean getShouldRevealEvaluation() {
        return false;
    }

    class Builder extends ImmutableGradingOptions.Builder {}
}
