package org.iatoki.judgels.sandalphon.grader;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.Entity;

@Entity(name = "sandalphon_grader")
@JidPrefix("SAGR")
public final class GraderModel extends JudgelsModel {

    public String name;
    public String secret;

    public GraderModel() {

    }

    public GraderModel(long id, String jid, String name) {
        this.id = id;
        this.jid = jid;
        this.name = name;
    }
}
