package judgels.persistence;

import java.util.Collection;
import java.util.Map;
import javax.persistence.metamodel.SingularAttribute;
import org.immutables.value.Value;

@Value.Immutable
public interface SelectAllOptions<M extends UnmodifiableModel> {
    int getPage();
    int getPageSize();
    Map<SingularAttribute<? super M, ?>, Object> getFilterColumnsEq();
    Map<SingularAttribute<? super M, ?>, Collection<?>> getFilterColumnsIn();
    SingularAttribute<? super M, ?> getOrderBy();
    OrderDir getOrderDir();

    class Builder<M extends UnmodifiableModel> extends ImmutableSelectAllOptions.Builder<M> {
        public Builder() {
            page(1);
            pageSize(20);
            orderBy(Model_.id);
            orderDir(OrderDir.ASC);
        }
    }
}
