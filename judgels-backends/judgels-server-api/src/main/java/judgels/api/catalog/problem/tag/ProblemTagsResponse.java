package judgels.api.catalog.problem.tag;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemTagsResponse.class)
public interface ProblemTagsResponse {
    List<ProblemTagCategory> getData();
    List<String> getTopicTags();

    class Builder extends ImmutableProblemTagsResponse.Builder {}
}
