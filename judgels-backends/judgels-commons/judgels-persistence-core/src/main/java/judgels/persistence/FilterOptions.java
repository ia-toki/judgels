package judgels.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.persistence.metamodel.SingularAttribute;
import org.immutables.value.Value;

@Value.Immutable
public interface FilterOptions<M extends UnmodifiableModel> {
    long getLastId();
    Map<SingularAttribute<? super M, ?>, Object> getColumnsEq();
    Map<SingularAttribute<? super M, ?>, Collection<?>> getColumnsIn();
    Map<SingularAttribute<? super M, String>, String> getColumnsLike();
    List<CustomPredicateFilter<M>> getCustomPredicates();

    class Builder<M extends UnmodifiableModel> extends ImmutableFilterOptions.Builder<M> {
        public Builder() {
            lastId(0);
        }
    }
}
