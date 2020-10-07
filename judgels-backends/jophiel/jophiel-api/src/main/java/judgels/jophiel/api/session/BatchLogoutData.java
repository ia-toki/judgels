package judgels.jophiel.api.session;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBatchLogoutData.class)
public interface BatchLogoutData {
    List<String> getUserJids();

    static BatchLogoutData of(List<String> userJids) {
        return ImmutableBatchLogoutData.builder()
                .userJids(userJids)
                .build();
    }
}
