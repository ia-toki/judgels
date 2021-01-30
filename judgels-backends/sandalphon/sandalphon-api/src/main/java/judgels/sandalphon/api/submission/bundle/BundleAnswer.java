package judgels.sandalphon.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBundleAnswer.class)
public interface BundleAnswer {
    Map<String, String> getAnswers();
    String getLanguageCode();

    class Builder extends ImmutableBundleAnswer.Builder {}
}
