package judgels.jophiel.api.user.info;

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
    Optional<String> getCountry();
    Optional<String> getHomeAddress();
    Optional<String> getShirtSize();

    Optional<String> getInstitutionName();
    Optional<String> getInstitutionCity();
    Optional<String> getInstitutionProvince();
    Optional<String> getInstitutionCountry();

    class Builder extends ImmutableUserInfo.Builder {}
}
