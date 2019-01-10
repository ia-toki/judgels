package org.iatoki.judgels.sandalphon.lesson.statement;

public final class LessonStatement {

    private String title;
    private String text;

    public LessonStatement(String title, String text) {
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
