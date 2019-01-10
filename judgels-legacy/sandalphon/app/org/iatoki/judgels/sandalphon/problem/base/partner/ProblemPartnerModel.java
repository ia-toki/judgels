package org.iatoki.judgels.sandalphon.problem.base.partner;

import org.iatoki.judgels.play.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sandalphon_problem_partner")
public final class ProblemPartnerModel extends AbstractModel {

    @Id
    @GeneratedValue
    public long id;

    public String problemJid;

    public String userJid;

    @Column(columnDefinition = "TEXT")
    public String baseConfig;

    @Column(columnDefinition = "TEXT")
    public String childConfig;
}
