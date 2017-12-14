package judgels.jophiel.api.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserInfo.class)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public interface UserInfo {
    Optional<String> getName();
    Optional<String> getGender();
    Optional<String> getStreetAddress();
    Optional<String> getPostalCode();
    Optional<String> getInstitution();
    Optional<String> getCity();
    Optional<String> getProvinceOrState();
    Optional<String> getCountry();
    Optional<String> getShirtSize();

    class Builder extends ImmutableUserInfo.Builder {}
}
