package org.iatoki.judgels.jerahmeel.chapter.dependency;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@Entity(name = "jerahmeel_chapter_dependency")
@Table(indexes = {
        @Index(columnList = "chapterJid,dependedChapterJid", unique = true)})
public final class ChapterDependencyModel extends Model {
    @Column(nullable = false)
    public String chapterJid;

    @Column(nullable = false)
    public String dependedChapterJid;
}
