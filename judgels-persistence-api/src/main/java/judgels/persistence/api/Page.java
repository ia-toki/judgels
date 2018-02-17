package judgels.persistence.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePage.class)
public interface Page<M> {
    long getCurrentPage();
    long getPageSize();
    long getTotalPages();
    long getTotalData();
    List<M> getData();

    class Builder<M> extends ImmutablePage.Builder<M> {}
}
