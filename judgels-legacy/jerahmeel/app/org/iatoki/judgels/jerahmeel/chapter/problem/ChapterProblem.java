package org.iatoki.judgels.jerahmeel.chapter.problem;

public final class ChapterProblem {

    private final long id;
    private final String chapterJid;
    private final String problemJid;
    private final String problemSecret;
    private final String alias;
    private final ChapterProblemType type;
    private final ChapterProblemStatus status;

    public ChapterProblem(long id, String chapterJid, String problemJid, String problemSecret, String alias, ChapterProblemType type, ChapterProblemStatus status) {
        this.id = id;
        this.chapterJid = chapterJid;
        this.problemJid = problemJid;
        this.problemSecret = problemSecret;
        this.alias = alias;
        this.type = type;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getChapterJid() {
        return chapterJid;
    }

    public String getProblemJid() {
        return problemJid;
    }

    public String getProblemSecret() {
        return problemSecret;
    }

    public String getAlias() {
        return alias;
    }

    public ChapterProblemType getType() {
        return type;
    }

    public ChapterProblemStatus getStatus() {
        return status;
    }
}
