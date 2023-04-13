package judgels.jophiel.api.profile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.jophiel.api.user.rating.UserRating;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProfile.class)
public interface Profile {
    String getUsername();
    Optional<String> getCountry();
    Optional<UserRating> getRating();

    class Builder extends ImmutableProfile.Builder {}
}
