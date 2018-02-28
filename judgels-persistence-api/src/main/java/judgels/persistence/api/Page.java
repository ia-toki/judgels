package judgels.persistence.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.function.Function;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePage.class)
public interface Page<M> {
    long getTotalData();
    List<M> getData();

    default <R> Page<R> mapData(Function<List<M>, List<R>> func) {
        return new Builder<R>()
                .totalData(getTotalData())
                .data(func.apply(getData()))
                .build();
    }

    class Builder<M> extends ImmutablePage.Builder<M> {}
}
