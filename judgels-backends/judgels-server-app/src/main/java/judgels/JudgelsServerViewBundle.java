package judgels;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.views.common.ViewBundle;
import java.util.Map;

public class JudgelsServerViewBundle extends ViewBundle<JudgelsServerApplicationConfiguration> {
    @Override
    public Map<String, Map<String, String>> getViewConfiguration(JudgelsServerApplicationConfiguration configuration) {
        return ImmutableMap.of(
                "freemarker", ImmutableMap.of(
                        "strict_syntax", "true",
                        "number_format", "computer"));
    }
}
