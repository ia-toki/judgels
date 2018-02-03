package judgels.persistence.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePage.class)
public interface Page<M> {
    long getTotalItems();
    long getTotalPages();
    List<M> getData();

    class Builder<M> extends ImmutablePage.Builder<M> {}
}
