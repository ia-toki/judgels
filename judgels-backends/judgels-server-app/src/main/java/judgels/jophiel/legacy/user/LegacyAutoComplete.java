package judgels.jophiel.legacy.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLegacyAutoComplete.class)
public interface LegacyAutoComplete {
    String getId();
    String getValue();
    String getLabel();

    class Builder extends ImmutableLegacyAutoComplete.Builder {}
}
