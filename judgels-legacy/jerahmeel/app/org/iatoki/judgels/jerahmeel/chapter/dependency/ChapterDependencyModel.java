package org.iatoki.judgels.jerahmeel.chapter.dependency;

import org.iatoki.judgels.play.model.AbstractModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "jerahmeel_chapter_dependency")
public final class ChapterDependencyModel extends AbstractModel {

    @Id
    @GeneratedValue
    public long id;

    public String chapterJid;

    public String dependedChapterJid;
}
