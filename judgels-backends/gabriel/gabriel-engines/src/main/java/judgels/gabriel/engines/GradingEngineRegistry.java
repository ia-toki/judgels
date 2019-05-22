package judgels.gabriel.engines;

import com.google.common.collect.ImmutableList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import judgels.gabriel.api.GradingEngine;
import judgels.gabriel.engines.batch.BatchGradingEngine;
import judgels.gabriel.engines.batch.BatchWithSubtasksGradingEngine;
import judgels.gabriel.engines.functional.FunctionalGradingEngine;
import judgels.gabriel.engines.functional.FunctionalWithSubtasksGradingEngine;
import judgels.gabriel.engines.interactive.InteractiveGradingEngine;
import judgels.gabriel.engines.interactive.InteractiveWithSubtasksGradingEngine;
import judgels.gabriel.engines.outputonly.OutputOnlyGradingEngine;
import judgels.gabriel.engines.outputonly.OutputOnlyWithSubtasksGradingEngine;

public class GradingEngineRegistry {
    private static final GradingEngineRegistry INSTANCE = new GradingEngineRegistry();
    private static final String DEFAULT_GRADING_ENGINE = "BatchWithSubtasks";

    private static final List<GradingEngine> ENGINES = ImmutableList.of(
            new BatchGradingEngine(),
            new BatchWithSubtasksGradingEngine(),
            new InteractiveGradingEngine(),
            new InteractiveWithSubtasksGradingEngine(),
            new OutputOnlyGradingEngine(),
            new OutputOnlyWithSubtasksGradingEngine(),
            new FunctionalGradingEngine(),
            new FunctionalWithSubtasksGradingEngine());

    private static final Map<String, GradingEngine> ENGINES_BY_SIMPLE_NAME = ENGINES.stream().collect(
            LinkedHashMap::new,
            (map, engine) -> map.put(getSimpleName(engine), engine),
            Map::putAll);

    private static final Map<String, String> ENGINE_NAMES_BY_SIMPLE_NAME = ENGINES.stream().collect(
            LinkedHashMap::new,
            (map, engine) -> map.put(getSimpleName(engine), engine.getName()),
            Map::putAll);

    private GradingEngineRegistry() {}

    public static GradingEngineRegistry getInstance() {
        return INSTANCE;
    }

    public GradingEngine get(String simpleName) {
        GradingEngine engine = ENGINES_BY_SIMPLE_NAME.get(simpleName);
        if (engine == null) {
            throw new IllegalArgumentException("Grading engine " + simpleName + " not found");
        }
        return engine;
    }

    public String getDefault() {
        return DEFAULT_GRADING_ENGINE;
    }

    public Map<String, String> getNamesMap() {
        return ENGINE_NAMES_BY_SIMPLE_NAME;
    }

    private static String getSimpleName(GradingEngine engine) {
        String name = engine.getClass().getSimpleName();
        return name.substring(0, name.length() - "GradingEngine".length());
    }
}
