package org.iatoki.judgels.sandalphon.problem.bundle.item;

public final class ItemChoice {

    private final String alias;
    private final String content;
    private final boolean isCorrect;

    public ItemChoice(String alias, String content, boolean isCorrect) {
        this.alias = alias;
        this.content = content;
        this.isCorrect = isCorrect;
    }

    public String getAlias() {
        return alias;
    }

    public String getContent() {
        return content;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
