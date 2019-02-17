package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemWorksheet.class)
public interface ProblemWorksheet {
    Optional<String> getReasonNotAllowedToSubmit();
    List<Item> getItems();

    class Builder extends ImmutableProblemWorksheet.Builder {}
}
