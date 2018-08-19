package judgels.jophiel.api.profile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBasicProfile.class)
public interface BasicProfile {
    String getUsername();
    Optional<Integer> getRating();
    Optional<String> getCountry();
    Optional<String> getName();

    class Builder extends ImmutableBasicProfile.Builder {}
}
