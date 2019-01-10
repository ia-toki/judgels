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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Verdict verdict = (Verdict) o;

        if (!code.equals(verdict.code)) {
            return false;
        }
        if (!name.equals(verdict.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
