package org.iatoki.judgels.jerahmeel.archive;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "jerahmeel_archive")
@JidPrefix("ARCH")
public final class ArchiveModel extends JudgelsModel {

    public String parentJid;

    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;
}
