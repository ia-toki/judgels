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
    private static final Class<? extends GradingEngine> DEFAULT_CLASS = BatchGradingEngine.class;

    private static final List<Class<? extends GradingEngine>> CLASSES = ImmutableList.of(
            BatchGradingEngine.class,
            BatchWithSubtasksGradingEngine.class,
            InteractiveGradingEngine.class,
            InteractiveWithSubtasksGradingEngine.class,
            OutputOnlyGradingEngine.class,
            OutputOnlyWithSubtasksGradingEngine.class,
            FunctionalGradingEngine.class,
            FunctionalWithSubtasksGradingEngine.class);

    private static final Map<String, Class<? extends GradingEngine>> CLASSES_BY_SIMPLE_NAME = CLASSES.stream().collect(
            LinkedHashMap::new,
            (map, clazz) -> map.put(getSimpleName(clazz), clazz),
            Map::putAll);

    private static final Map<String, String> ENGINE_NAMES_BY_SIMPLE_NAME = CLASSES.stream().collect(
            LinkedHashMap::new,
            (map, clazz) -> map.put(getSimpleName(clazz), newInstance(clazz).getName()),
            Map::putAll);

    private GradingEngineRegistry() {}

    public static GradingEngineRegistry getInstance() {
        return INSTANCE;
    }

    public GradingEngine get(String simpleName) {
        Class<? extends GradingEngine> clazz = CLASSES_BY_SIMPLE_NAME.get(simpleName);
        if (clazz == null) {
            throw new IllegalArgumentException("Grading engine " + simpleName + " not found");
        }
        return newInstance(clazz);
    }

    public String getDefault() {
        return getSimpleName(DEFAULT_CLASS);
    }

    public Map<String, String> getNamesMap() {
        return ENGINE_NAMES_BY_SIMPLE_NAME;
    }

    private static String getSimpleName(Class<? extends GradingEngine> engine) {
        String name = engine.getSimpleName();
        return name.substring(0, name.length() - "GradingEngine".length());
    }

    private static GradingEngine newInstance(Class<? extends GradingEngine> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
