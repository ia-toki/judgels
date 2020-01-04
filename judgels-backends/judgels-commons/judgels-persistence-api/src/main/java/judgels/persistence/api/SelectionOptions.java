package judgels.persistence.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSelectionOptions.class)
public interface SelectionOptions {
    SelectionOptions DEFAULT_PAGED = new Builder().pageSize(20).build();
    SelectionOptions DEFAULT_ALL = new Builder().pageSize(0).build();

    int getPage();
    int getPageSize();
    String getOrderBy();
    OrderDir getOrderDir();
    String getOrderBy2();
    Optional<OrderDir> getOrderDir2();

    class Builder extends ImmutableSelectionOptions.Builder {
        public Builder() {
            page(1);
            pageSize(0);
            orderBy("id");
            orderDir(OrderDir.DESC);
            orderBy2("id");
        }
    }
}
