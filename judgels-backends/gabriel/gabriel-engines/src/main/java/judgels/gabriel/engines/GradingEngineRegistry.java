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
    private static final Class<? extends GradingEngine> DEFAULT_GRADING_ENGINE = BatchGradingEngine.class;

    private static final List<Class<? extends GradingEngine>> ENGINES = ImmutableList.of(
            BatchGradingEngine.class,
            BatchWithSubtasksGradingEngine.class,
            InteractiveGradingEngine.class,
            InteractiveWithSubtasksGradingEngine.class,
            OutputOnlyGradingEngine.class,
            OutputOnlyWithSubtasksGradingEngine.class,
            FunctionalGradingEngine.class,
            FunctionalWithSubtasksGradingEngine.class);

    private static final Map<String, Class<? extends GradingEngine>> ENGINES_BY_SIMPLE_NAME = ENGINES.stream().collect(
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
        Class<? extends GradingEngine> engine = ENGINES_BY_SIMPLE_NAME.get(simpleName);
        if (engine == null) {
            throw new IllegalArgumentException("Grading engine " + simpleName + " not found");
        }
        try {
            return engine.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDefault() {
        return getSimpleName(DEFAULT_GRADING_ENGINE);
    }

    public Map<String, String> getNamesMap() {
        return ENGINE_NAMES_BY_SIMPLE_NAME;
    }

    private static String getSimpleName(Class<? extends GradingEngine> engine) {
        String name = engine.getSimpleName();
        return name.substring(0, name.length() - "GradingEngine".length());
    }
}
