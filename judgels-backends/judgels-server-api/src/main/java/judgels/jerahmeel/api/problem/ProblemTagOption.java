package judgels.jerahmeel.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemTagOption.class)
public interface ProblemTagOption {
    String getLabel();
    String getValue();
    int getCount();

    class Builder extends ImmutableProblemTagOption.Builder {}
}
