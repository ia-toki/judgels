package judgels.api.catalog.problem.bundle.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.catalog.problem.bundle.ItemType;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemItemCreateData.class)
public interface ProblemItemCreateData {
    ItemType getType();

    class Builder extends ImmutableProblemItemCreateData.Builder {}
}
