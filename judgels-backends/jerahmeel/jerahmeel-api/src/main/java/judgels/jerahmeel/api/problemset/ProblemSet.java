package judgels.jerahmeel.api.problemset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSet.class)
public interface ProblemSet {
    long getId();
    String getJid();
    String getSlug();
    String getName();
    String getDescription();

    class Builder extends ImmutableProblemSet.Builder {}
}
