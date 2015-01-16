package org.iatoki.judgels.gabriel;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public final class GradingRegistry {
    private static GradingRegistry INSTANCE;

    private final Map<String, GradingExecutor> registry;

    private GradingRegistry() {
        this.registry = Maps.newHashMap();

        populateGradingTypes();
    }

    public GradingExecutor getGradingExecutor(String gradingType) {
        if (!registry.containsKey(gradingType)) {
            throw new IllegalArgumentException("Grading method " + gradingType + " unknown");
        }
        return registry.get(gradingType);
    }

    public Map<String, String> getGradingTypes() {
        return registry.keySet().stream().collect(Collectors.toMap(e -> e, e -> registry.get(e).getName()));
    }

    public static GradingRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GradingRegistry();
        }
        return INSTANCE;
    }

    private void populateGradingTypes() {
        for (GradingExecutor gradingExecutor : ServiceLoader.load(GradingExecutor.class)) {
            registry.put(gradingExecutor.getClass().getSimpleName(), gradingExecutor);
        }
    }
}
