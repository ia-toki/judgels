package org.iatoki.judgels.gabriel;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public final class GraderRegistry {
    private static GraderRegistry INSTANCE;

    private final Map<String, Grader> registry;

    private GraderRegistry() {
        this.registry = Maps.newHashMap();

        populateGraders();
    }

    public Grader getGrader(String gradingType) {
        if (!registry.containsKey(gradingType)) {
            throw new IllegalArgumentException("Grading type " + gradingType + " unknown");
        }
        return registry.get(gradingType);
    }

    public static GraderRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GraderRegistry();
        }
        return INSTANCE;
    }

    public  Map<String, String> getGradingTypes() {
        return registry.keySet().stream().collect(Collectors.toMap(e -> e, e -> registry.get(e).getName()));
    }

    private void populateGraders() {
        for (Grader grader : ServiceLoader.load(Grader.class)) {
            registry.put(getGraderSimpleName(grader), grader);
        }
    }

    private String getGraderSimpleName(Grader grader) {
        String name = grader.getClass().getSimpleName();
        return name.substring(0, name.length() - "Grader".length());
    }
}
