package tlx.api.curriculum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCurriculumUpdateData.class)
public interface CurriculumUpdateData {
    Optional<String> getName();
    Optional<String> getDescription();

    class Builder extends ImmutableCurriculumUpdateData.Builder {}
}
