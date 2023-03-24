package judgels.michael.problem.programming.grading.config;

import java.util.HashMap;
import java.util.Map;

public class GradingConfigAdapterRegistry {
    private static final GradingConfigAdapterRegistry INSTANCE = new GradingConfigAdapterRegistry();

    private final Map<String, GradingConfigAdapter> registry = new HashMap<>();

    private GradingConfigAdapterRegistry() {
        registry.put("Batch", new BatchGradingConfigAdapter());
    }

    public static GradingConfigAdapterRegistry getInstance() {
        return INSTANCE;
    }

    public GradingConfigAdapter get(String gradingEngine) {
        return registry.get(gradingEngine);
    }
}
