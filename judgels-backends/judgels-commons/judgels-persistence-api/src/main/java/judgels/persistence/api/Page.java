package judgels.persistence.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.function.Function;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePage.class)
public interface Page<M> {
    long getTotalCount();
    List<M> getPage();

    @Value.Default
    @JsonInclude(Include.NON_EMPTY)
    default long getPageNumber() {
        return 0;
    }

    @Value.Default
    @JsonInclude(Include.NON_EMPTY)
    default long getPageSize() {
        return 0;
    }

    default <R> Page<R> mapPage(Function<List<M>, List<R>> func) {
        return new Builder<R>()
                .totalCount(getTotalCount())
                .page(func.apply(getPage()))
                .pageNumber(getPageNumber())
                .pageSize(getPageSize())
                .build();
    }

    class Builder<M> extends ImmutablePage.Builder<M> {}
}
