package judgels.uriel.api.contest.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestProblemsSetResponse.class)
public interface ContestProblemsSetResponse {
    Set<String> getSetSlugs();

    class Builder extends ImmutableContestProblemsSetResponse.Builder {}
}
