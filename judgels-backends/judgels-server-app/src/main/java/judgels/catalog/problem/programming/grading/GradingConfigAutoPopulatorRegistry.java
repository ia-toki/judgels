package judgels.catalog.problem.programming.grading;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradingConfigAutoPopulatorRegistry {
    private static final List<GradingConfigAutoPopulator> AUTO_POPULATORS = ImmutableList.of(
            new BatchGradingConfigAutoPopulator(),
            new BatchWithSubtasksGradingConfigAutoPopulator(),
            new InteractiveGradingConfigAutoPopulator(),
            new InteractiveWithSubtasksGradingConfigAutoPopulator(),
            new OutputOnlyGradingConfigAutoPopulator(),
            new OutputOnlyWithSubtasksGradingConfigAutoPopulator(),
            new FunctionalGradingConfigAutoPopulator(),
            new FunctionalWithSubtasksGradingConfigAutoPopulator());

    private static final GradingConfigAutoPopulatorRegistry INSTANCE = new GradingConfigAutoPopulatorRegistry();

    private final Map<String, GradingConfigAutoPopulator> autoPopulatorsByEngineName = new HashMap<>();

    private GradingConfigAutoPopulatorRegistry() {
        for (GradingConfigAutoPopulator autoPopulator : AUTO_POPULATORS) {
            autoPopulatorsByEngineName.put(getEngineName(autoPopulator), autoPopulator);
        }
    }

    public static GradingConfigAutoPopulatorRegistry getInstance() {
        return INSTANCE;
    }

    public GradingConfigAutoPopulator get(String engine) {
        return autoPopulatorsByEngineName.get(engine);
    }

    private static String getEngineName(GradingConfigAutoPopulator autoPopulator) {
        String name = autoPopulator.getClass().getSimpleName();
        return name.substring(0, name.length() - "GradingConfigAutoPopulator".length());
    }
}
