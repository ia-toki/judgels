package org.iatoki.judgels.play.jid;

import javax.persistence.MappedSuperclass;
import judgels.persistence.Model;

@MappedSuperclass
public abstract class AbstractJidCacheModel extends Model {
    public String jid;

    public String displayName;
}
