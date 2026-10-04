package judgels.api.catalog.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import judgels.api.catalog.problem.ProblemStatement;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemWorksheet.class)
public interface ProblemWorksheet {
    ProblemStatement getStatement();
    Optional<String> getReasonNotAllowedToSubmit();
    List<Item> getItems();

    class Builder extends ImmutableProblemWorksheet.Builder {}
}
