package org.iatoki.judgels.gabriel;

public enum GradingType {
    BATCH_SUBTASK("Batch with Subtasks");

    private String name;

    GradingType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
