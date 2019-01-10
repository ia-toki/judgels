package org.iatoki.judgels.sandalphon.client;

import org.iatoki.judgels.play.jid.JidPrefix;
import org.iatoki.judgels.play.model.AbstractJudgelsModel;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sandalphon_client")
@JidPrefix("SACL")
public final class ClientModel extends AbstractJudgelsModel {

    public String name;

    public String secret;

    public ClientModel() {

    }

    public ClientModel(long id, String jid, String name) {
        this.id = id;
        this.jid = jid;
        this.name = name;
    }
}
