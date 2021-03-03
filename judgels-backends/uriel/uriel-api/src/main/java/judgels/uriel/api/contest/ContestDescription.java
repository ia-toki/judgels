package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestDescription.class)
public interface ContestDescription {
    String getDescription();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableContestDescription.Builder {}
}
