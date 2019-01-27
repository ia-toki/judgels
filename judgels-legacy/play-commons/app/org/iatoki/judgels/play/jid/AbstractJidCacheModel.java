package org.iatoki.judgels.play.jid;

import judgels.persistence.Model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractJidCacheModel extends Model {
    public String jid;

    public String displayName;
}
