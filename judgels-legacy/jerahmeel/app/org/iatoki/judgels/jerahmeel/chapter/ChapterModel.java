package org.iatoki.judgels.jerahmeel.chapter;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "jerahmeel_chapter")
@JidPrefix("SESS")
public final class ChapterModel extends JudgelsModel {

    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;
}
