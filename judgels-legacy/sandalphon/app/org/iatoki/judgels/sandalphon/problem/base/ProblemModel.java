package org.iatoki.judgels.sandalphon.problem.base;

import judgels.persistence.JidChildPrefixes;
import judgels.persistence.JudgelsModel;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "sandalphon_problem")
@JidChildPrefixes({"PROG", "BUND"})
public final class ProblemModel extends JudgelsModel {

    public String slug;

    @Column(columnDefinition = "TEXT")
    public String additionalNote;
}
