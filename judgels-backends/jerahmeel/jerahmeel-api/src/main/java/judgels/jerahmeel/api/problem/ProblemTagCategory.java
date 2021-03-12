package judgels.jerahmeel.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemTagCategory.class)
public interface ProblemTagCategory {
    String getTitle();
    List<ProblemTagOption> getOptions();

    class Builder extends ImmutableProblemTagCategory.Builder {}
}
