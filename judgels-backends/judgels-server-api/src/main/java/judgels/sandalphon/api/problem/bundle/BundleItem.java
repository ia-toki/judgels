package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBundleItem.class)
public interface BundleItem {
    String getJid();
    ItemType getType();
    Optional<Integer> getNumber();
    String getMeta();

    class Builder extends ImmutableBundleItem.Builder {}
}
