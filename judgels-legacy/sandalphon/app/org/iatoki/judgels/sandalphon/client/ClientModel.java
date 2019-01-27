package org.iatoki.judgels.sandalphon.client;

import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;

import javax.persistence.Entity;

@Entity(name = "sandalphon_client")
@JidPrefix("SACL")
public final class ClientModel extends JudgelsModel {

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
