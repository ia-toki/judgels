package org.iatoki.judgels.jerahmeel.chapter.problem;

import judgels.persistence.Model;

import javax.persistence.Entity;

@Entity(name = "jerahmeel_chapter_problem")
public final class ChapterProblemModel extends Model {
    public String chapterJid;

    public String problemJid;

    public String alias;

    public String type;

    public String status;
}
