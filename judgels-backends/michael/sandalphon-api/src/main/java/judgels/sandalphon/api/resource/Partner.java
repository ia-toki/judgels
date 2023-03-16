package judgels.sandalphon.api.resource;

import org.immutables.value.Value;

@Value.Immutable
public interface Partner {
    String getUserJid();
    PartnerPermission getPermission();

    class Builder extends ImmutablePartner.Builder {}
}
