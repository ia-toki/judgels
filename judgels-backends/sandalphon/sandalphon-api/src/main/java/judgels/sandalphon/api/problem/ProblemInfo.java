package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemInfo.class)
public interface ProblemInfo {
    Optional<String> getSlug();
    ProblemType getType();
    String getDefaultLanguage();
    Map<String, String> getTitlesByLanguage();

    class Builder extends ImmutableProblemInfo.Builder {}
}
