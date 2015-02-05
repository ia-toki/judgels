package org.iatoki.judgels.gabriel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public final class GradingEngineRegistry {
    private static GradingEngineRegistry INSTANCE;

    private final Map<String, GradingEngine> registry;

    private GradingEngineRegistry() {
        this.registry = Maps.newLinkedHashMap();

        populateEngines();
    }

    public GradingEngine getEngine(String gradingEngine) {
        if (!registry.containsKey(gradingEngine)) {
            throw new IllegalArgumentException("Grading engine " + gradingEngine + " unknown");
        }
        return registry.get(gradingEngine);
    }

    public static GradingEngineRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GradingEngineRegistry();
        }
        return INSTANCE;
    }

    public Map<String, String> getGradingEngines() {
        return Maps.transformValues(registry, engine -> engine.getName());
    }

    private void populateEngines() {
        List<GradingEngine> engines = Lists.newArrayList(ServiceLoader.load(GradingEngine.class));

        Collections.sort(engines, (GradingEngine o1, GradingEngine o2) -> o1.getName().compareTo(o2.getName()));

        for (GradingEngine engine : engines) {
            registry.put(getEngineSimpleName(engine), engine);
        }
    }

    private String getEngineSimpleName(GradingEngine gradingEngine) {
        String name = gradingEngine.getClass().getSimpleName();
        return name.substring(0, name.length() - "GradingEngine".length());
    }
}
