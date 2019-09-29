package org.iatoki.judgels.jerahmeel.chapter.problem;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@Entity(name = "jerahmeel_chapter_problem")
@Table(indexes = {
        @Index(columnList = "chapterJid,problemJid", unique = true),
        @Index(columnList = "chapterJid,alias", unique = true)})
public final class ChapterProblemModel extends Model {
    @Column(nullable = false)
    public String chapterJid;

    @Column(nullable = false)
    public String problemJid;

    @Column(nullable = false)
    public String alias;

    @Column(nullable = false)
    public String type;

    @Column(nullable = false)
    public String status;
}
