package judgels.jophiel.api.web;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableWebConfig.class)
public interface WebConfig {
    List<String> getAnnouncements();

    class Builder extends ImmutableWebConfig.Builder {}
}
