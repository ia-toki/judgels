package judgels.jerahmeel.api.chapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChaptersResponse.class)
public interface ChaptersResponse {
    List<Chapter> getData();

    class Builder extends ImmutableChaptersResponse.Builder {}
}
