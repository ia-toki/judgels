package judgels.persistence.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.function.Function;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePage.class)
public interface Page<M> {
    long getTotalCount();
    List<M> getPage();

    default <R> Page<R> mapPage(Function<List<M>, List<R>> func) {
        return new Builder<R>()
                .totalCount(getTotalCount())
                .page(func.apply(getPage()))
                .build();
    }

    class Builder<M> extends ImmutablePage.Builder<M> {}
}
