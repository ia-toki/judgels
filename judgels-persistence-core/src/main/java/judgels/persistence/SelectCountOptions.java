package judgels.persistence;

import java.util.Map;
import javax.persistence.metamodel.SingularAttribute;
import org.immutables.value.Value;

@Value.Immutable
public interface SelectCountOptions<M extends UnmodifiableModel> {
    Map<SingularAttribute<? super M, ?>, Object> getFilterColumnsEq();
    Map<SingularAttribute<? super M, ?>, Object> getFilterColumnsIn();

    class Builder<M extends UnmodifiableModel> extends ImmutableSelectCountOptions.Builder<M> {}
}
