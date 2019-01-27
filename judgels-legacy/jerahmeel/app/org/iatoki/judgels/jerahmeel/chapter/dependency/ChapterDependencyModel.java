package org.iatoki.judgels.jerahmeel.chapter.dependency;

import judgels.persistence.Model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "jerahmeel_chapter_dependency")
public final class ChapterDependencyModel extends Model {
    public String chapterJid;

    public String dependedChapterJid;
}
