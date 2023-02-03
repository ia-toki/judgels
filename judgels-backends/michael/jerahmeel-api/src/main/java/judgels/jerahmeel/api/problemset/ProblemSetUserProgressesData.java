package judgels.jerahmeel.api.problemset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetUserProgressesData.class)
public interface ProblemSetUserProgressesData {
    List<String> getUsernames();
    List<String> getProblemSetSlugs();

    class Builder extends ImmutableProblemSetUserProgressesData.Builder {}
}
