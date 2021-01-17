package org.iatoki.judgels.play.jid;

import java.util.Collection;
import java.util.List;
import judgels.persistence.Dao;

public interface BaseJidCacheDao<M extends AbstractJidCacheModel> extends Dao<M> {

    M createJidCacheModel();

    boolean existsByJid(String jid);

    M findByJid(String jid);

    List<M> getByJids(Collection<String> jids);
}
