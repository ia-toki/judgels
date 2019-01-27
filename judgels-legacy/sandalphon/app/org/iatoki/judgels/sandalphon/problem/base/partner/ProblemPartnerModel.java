package org.iatoki.judgels.sandalphon.problem.base.partner;

import judgels.persistence.Model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "sandalphon_problem_partner")
public final class ProblemPartnerModel extends Model {
    public String problemJid;

    public String userJid;

    @Column(columnDefinition = "TEXT")
    public String baseConfig;

    @Column(columnDefinition = "TEXT")
    public String childConfig;
}
