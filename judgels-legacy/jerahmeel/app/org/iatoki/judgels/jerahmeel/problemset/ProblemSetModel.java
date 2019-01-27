package org.iatoki.judgels.jerahmeel.problemset;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "jerahmeel_problem_set")
@JidPrefix("PRSE")
public final class ProblemSetModel extends JudgelsModel {

    public String archiveJid;

    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;
}
