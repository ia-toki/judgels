package org.iatoki.judgels.gabriel;

import com.google.common.collect.Maps;
import org.iatoki.judgels.gabriel.graders.BatchWithSubtaskGrader;

import java.util.EnumMap;

public final class GraderRegistry {
    private static GraderRegistry INSTANCE;

    private final EnumMap<GradingType, Grader> registry;

    private GraderRegistry() {
        this.registry = Maps.newEnumMap(GradingType.class);

        populateGraders();
    }

    public Grader getGrader(GradingType gradingType) {
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

    private void populateGraders() {
        registry.put(GradingType.BATCH_SUBTASK, new BatchWithSubtaskGrader());
    }
}
