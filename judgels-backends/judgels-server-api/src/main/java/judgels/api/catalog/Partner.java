package judgels.api.catalog;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePartner.class)
public interface Partner {
    String getUserJid();
    PartnerPermission getPermission();

    class Builder extends ImmutablePartner.Builder {}
}
