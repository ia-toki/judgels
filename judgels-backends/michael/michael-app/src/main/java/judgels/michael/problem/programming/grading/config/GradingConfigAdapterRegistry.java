package judgels.michael.problem.programming.grading.config;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradingConfigAdapterRegistry {
    private static final List<GradingConfigAdapter> ADAPTERS = ImmutableList.of(
            new BatchGradingConfigAdapter(),
            new BatchWithSubtasksGradingConfigAdapter(),
            new InteractiveGradingConfigAdapter(),
            new InteractiveWithSubtasksGradingConfigAdapter(),
            new OutputOnlyGradingConfigAdapter(),
            new OutputOnlyWithSubtasksGradingConfigAdapter(),
            new FunctionalGradingConfigAdapter(),
            new FunctionalWithSubtasksGradingConfigAdapter());

    private static final GradingConfigAdapterRegistry INSTANCE = new GradingConfigAdapterRegistry();

    private final Map<String, GradingConfigAdapter> adaptersByEngineName = new HashMap<>();

    private GradingConfigAdapterRegistry() {
        for (GradingConfigAdapter adapter : ADAPTERS) {
            adaptersByEngineName.put(getEngineName(adapter), adapter);
        }
    }

    public static GradingConfigAdapterRegistry getInstance() {
        return INSTANCE;
    }

    public GradingConfigAdapter get(String engine) {
        return adaptersByEngineName.get(engine);
    }

    private static String getEngineName(GradingConfigAdapter adapter) {
        String name = adapter.getClass().getSimpleName();
        return name.substring(0, name.length() - "GradingConfigAdapter".length());
    }
}
