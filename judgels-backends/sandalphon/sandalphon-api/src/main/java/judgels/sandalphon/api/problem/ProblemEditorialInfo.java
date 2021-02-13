package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemEditorialInfo.class)
public interface ProblemEditorialInfo {
    String getText();
    String getDefaultLanguage();
    Set<String> getLanguages();

    class Builder extends ImmutableProblemEditorialInfo.Builder {}
}
