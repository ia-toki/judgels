package judgels.sandalphon.api.problem.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSkeleton.class)
public interface ProblemSkeleton {
    Set<String> getLanguages();
    byte[] getContent();

    class Builder extends ImmutableProblemSkeleton.Builder {}
}
