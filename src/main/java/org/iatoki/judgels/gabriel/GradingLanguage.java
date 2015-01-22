package org.iatoki.judgels.gabriel;

public enum GradingLanguage {
    CPP("C++");

    private String name;

    GradingLanguage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
