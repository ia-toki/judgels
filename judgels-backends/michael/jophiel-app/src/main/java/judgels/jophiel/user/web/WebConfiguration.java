package judgels.jophiel.user.web;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableWebConfiguration.class)
public interface WebConfiguration {
    WebConfiguration DEFAULT = new Builder().build();

    List<String> getAnnouncements();

    class Builder extends ImmutableWebConfiguration.Builder {}
}
