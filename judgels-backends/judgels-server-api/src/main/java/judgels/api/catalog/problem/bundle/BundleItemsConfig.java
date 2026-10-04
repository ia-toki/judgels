package judgels.api.catalog.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBundleItemsConfig.class)
public interface BundleItemsConfig {
    List<BundleItem> getItemList();

    class Builder extends ImmutableBundleItemsConfig.Builder {}
}
