package org.iatoki.judgels.jerahmeel.chapter.dependency;

public final class ChapterDependency {

    private final long id;
    private final String chapterJid;
    private final String dependedChapterJid;
    private final String dependedChapterName;

    public ChapterDependency(long id, String chapterJid, String dependedChapterJid, String dependedChapterName) {
        this.id = id;
        this.chapterJid = chapterJid;
        this.dependedChapterJid = dependedChapterJid;
        this.dependedChapterName = dependedChapterName;
    }

    public long getId() {
        return id;
    }

    public String getChapterJid() {
        return chapterJid;
    }

    public String getDependedChapterJid() {
        return dependedChapterJid;
    }

    public String getDependedChapterName() {
        return dependedChapterName;
    }
}
