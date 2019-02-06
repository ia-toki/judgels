package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBundleProblemWorksheet.class)
public interface BundleProblemWorksheet {
    Optional<String> getReasonNotAllowedToSubmit();
    List<ProblemItem> getItems();

    class Builder extends ImmutableBundleProblemWorksheet.Builder {}
}
