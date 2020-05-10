package org.iatoki.judgels.sandalphon.problem.base.statement;

public final class ProblemStatement {

    private String title;
    private String text;

    public ProblemStatement(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
