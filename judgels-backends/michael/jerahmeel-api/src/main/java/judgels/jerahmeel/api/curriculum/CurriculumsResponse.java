package judgels.jerahmeel.api.curriculum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCurriculumsResponse.class)
public interface CurriculumsResponse {
    List<Curriculum> getData();

    class Builder extends ImmutableCurriculumsResponse.Builder {}
}
