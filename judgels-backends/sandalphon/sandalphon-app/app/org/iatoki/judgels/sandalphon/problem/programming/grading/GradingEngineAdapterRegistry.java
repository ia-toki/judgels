package org.iatoki.judgels.sandalphon.problem.programming.grading;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.ServiceLoader;

public final class GradingEngineAdapterRegistry {

    private static GradingEngineAdapterRegistry INSTANCE;

    private final Map<String, GradingEngineAdapter> registry;

    private GradingEngineAdapterRegistry() {
        this.registry = Maps.newHashMap();

        populateAdapters();
    }

    public static GradingEngineAdapterRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GradingEngineAdapterRegistry();
        }
        return INSTANCE;
    }

    public GradingEngineAdapter getByGradingEngineName(String gradingEngineName) {
        return registry.get(gradingEngineName);
    }

    private void populateAdapters() {
        for (GradingEngineAdapter adapter : ServiceLoader.load(GradingEngineAdapter.class)) {
            for (String supportedGradingEngineName : adapter.getSupportedGradingEngineNames()) {
                registry.put(supportedGradingEngineName, adapter);
            }
        }
    }
}
