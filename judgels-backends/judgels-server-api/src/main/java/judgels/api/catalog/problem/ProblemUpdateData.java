package judgels.api.catalog.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemUpdateData.class)
public interface ProblemUpdateData {
    String getSlug();
    String getAdditionalNote();
    Map<ProblemSetterRole, List<String>> getSetterUsernamesMap();
    Set<String> getTopicTags();

    class Builder extends ImmutableProblemUpdateData.Builder {}
}
