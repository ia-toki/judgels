package judgels.jophiel.api.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserProfile.class)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public interface UserProfile {
    Optional<String> getName();
    Optional<String> getGender();
    Optional<String> getNationality();
    Optional<String> getHomeAddress();

    Optional<String> getInstitution();
    Optional<String> getCity();
    Optional<String> getProvinceOrState();
    Optional<String> getCountry();

    Optional<String> getShirtSize();

    class Builder extends ImmutableUserProfile.Builder {}
}
