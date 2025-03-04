package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableItem.class)
public interface Item {
    String getJid();
    ItemType getType();
    Optional<Integer> getNumber();
    String getMeta();
    ItemConfig getConfig();

    class Builder extends ImmutableItem.Builder {}
}
