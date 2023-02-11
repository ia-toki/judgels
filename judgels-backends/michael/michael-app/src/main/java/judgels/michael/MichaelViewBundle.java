package judgels.michael;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.views.ViewBundle;
import java.util.Map;

public class MichaelViewBundle extends ViewBundle<MichaelApplicationConfiguration> {
    @Override
    public Map<String, Map<String, String>> getViewConfiguration(MichaelApplicationConfiguration configuration) {
        return ImmutableMap.of(
                "freemarker", ImmutableMap.of(
                        "strict_syntax", "true"));
    }
}
