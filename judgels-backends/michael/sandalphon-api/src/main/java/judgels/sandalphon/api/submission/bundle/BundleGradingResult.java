package judgels.sandalphon.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBundleGradingResult.class)
public interface BundleGradingResult {
    double getScore();
    Map<String, ItemGradingResult> getDetails();

    class Builder extends ImmutableBundleGradingResult.Builder {}
}
