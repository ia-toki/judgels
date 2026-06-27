package judgels.api.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSettings.class)
public interface Settings {
    AppSettings getApp();
    HomeSettings getHome();
    SessionSettings getSession();

    class Builder extends ImmutableSettings.Builder {}
}
