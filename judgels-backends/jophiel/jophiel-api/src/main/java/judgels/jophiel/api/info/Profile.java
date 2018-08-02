package judgels.jophiel.api.info;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProfile.class)
public interface Profile {
    String getUsername();
    Optional<String> getNationality();
    Optional<Integer> getRating();

    class Builder extends ImmutableProfile.Builder {}
}
