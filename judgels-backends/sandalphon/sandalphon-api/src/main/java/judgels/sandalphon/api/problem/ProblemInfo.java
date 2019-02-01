package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemInfo.class)
public interface ProblemInfo {
    String getSlug();
    String getDefaultLanguage();
    Map<String, String> getTitlesByLanguage();

    class Builder extends ImmutableProblemInfo.Builder {}
}
