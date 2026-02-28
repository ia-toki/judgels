package judgels.persistence.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.function.Function;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCursorPage.class)
public interface CursorPage<M> {
    List<M> getPage();
    boolean getHasNextPage();
    boolean getHasPreviousPage();

    default <R> CursorPage<R> mapPage(Function<List<M>, List<R>> func) {
        return new Builder<R>()
                .page(func.apply(getPage()))
                .hasNextPage(getHasNextPage())
                .hasPreviousPage(getHasPreviousPage())
                .build();
    }

    class Builder<M> extends ImmutableCursorPage.Builder<M> {}
}
