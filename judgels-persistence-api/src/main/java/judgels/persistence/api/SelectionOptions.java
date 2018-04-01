package judgels.persistence.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSelectionOptions.class)
public interface SelectionOptions {
    int getPage();
    int getPageSize();
    String getOrderBy();
    OrderDir getOrderDir();

    class Builder extends ImmutableSelectionOptions.Builder {
        public Builder() {
            page(1);
            pageSize(20);
            orderBy("id");
            orderDir(OrderDir.ASC);
        }
    }
}
