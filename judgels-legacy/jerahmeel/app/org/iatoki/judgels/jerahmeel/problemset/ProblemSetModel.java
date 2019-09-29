package org.iatoki.judgels.jerahmeel.problemset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

@Entity(name = "jerahmeel_problem_set")
@Table(indexes = {
        @Index(columnList = "archiveJid"),
        @Index(columnList = "name"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "updatedAt")})
@JidPrefix("PRSE")
public final class ProblemSetModel extends JudgelsModel {
    @Column(nullable = false)
    public String archiveJid;

    @Column(nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String description;
}
