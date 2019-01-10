package org.iatoki.judgels.sandalphon.problem.base;

import org.iatoki.judgels.play.jid.JidChildPrefixes;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sandalphon_problem")
@JidChildPrefixes({"PROG", "BUND"})
public final class ProblemModel extends AbstractJudgelsModel {

    public String slug;

    @Column(columnDefinition = "TEXT")
    public String additionalNote;
}
