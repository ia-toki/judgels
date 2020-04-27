package judgels.jerahmeel.api.problemset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetUpdateData.class)
public interface ProblemSetUpdateData {
    Optional<String> getSlug();
    Optional<String> getName();
    Optional<String> getArchiveSlug();
    Optional<String> getDescription();

    class Builder extends ImmutableProblemSetUpdateData.Builder {}
}
