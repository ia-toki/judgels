package org.iatoki.judgels.jerahmeel.archive;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "jerahmeel_archive")
@JidPrefix("ARCH")
public final class ArchiveModel extends AbstractJudgelsModel {

    public String parentJid;

    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;
}
