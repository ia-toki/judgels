package judgels.jerahmeel.api.problemset;

import org.immutables.value.Value;

@Value.Immutable
public interface ProblemSetCreateData {
    String getSlug();

    class Builder extends ImmutableProblemSetCreateData.Builder {}
}
