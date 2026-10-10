package judgels.api.catalog.problem.bundle.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import judgels.api.catalog.problem.bundle.Item;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemItemsResponse.class)
public interface ProblemItemsResponse {
    List<Item> getData();

    class Builder extends ImmutableProblemItemsResponse.Builder {}
}
