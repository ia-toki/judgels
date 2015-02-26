package org.iatoki.judgels.gabriel;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public final class GradingEngineRegistry {
    private static GradingEngineRegistry INSTANCE;

    private final Map<String, Class<? extends GradingEngine>> registry;

    private final Map<String, String> gradingEngineNames;

    private GradingEngineRegistry() {
        this.registry = Maps.newLinkedHashMap();
        this.gradingEngineNames = Maps.newLinkedHashMap();

        populateEngines();
    }

    public GradingEngine getEngine(String gradingEngine) {
        if (!registry.containsKey(gradingEngine)) {
            throw new IllegalArgumentException("Grading engine " + gradingEngine + " unknown");
        }

        Class<? extends GradingEngine> clazz = registry.get(gradingEngine);

        try {
            return clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public static GradingEngineRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GradingEngineRegistry();
        }
        return INSTANCE;
    }

    public Map<String, String> getGradingEngines() {
        return ImmutableMap.copyOf(gradingEngineNames);
    }

    private void populateEngines() {
        List<GradingEngine> engines = Lists.newArrayList(ServiceLoader.load(GradingEngine.class));

        Collections.sort(engines, (GradingEngine o1, GradingEngine o2) -> o1.getName().compareTo(o2.getName()));

        for (GradingEngine engine : engines) {
            registry.put(getEngineSimpleName(engine), engine.getClass());
            gradingEngineNames.put(getEngineSimpleName(engine), engine.getName());
        }
    }

    private String getEngineSimpleName(GradingEngine gradingEngine) {
        String name = gradingEngine.getClass().getSimpleName();
        return name.substring(0, name.length() - "GradingEngine".length());
    }
}
