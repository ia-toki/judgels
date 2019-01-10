package org.iatoki.judgels;

public final class AutoComplete {

    private final String id;
    private final String value;
    private final String label;

    public AutoComplete(String id, String value, String label) {
        this.id = id;
        this.value = value;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
