package org.iatoki.judgels.gabriel;

public final class Verdict {
    private final String code;
    private final String name;

    public Verdict(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
