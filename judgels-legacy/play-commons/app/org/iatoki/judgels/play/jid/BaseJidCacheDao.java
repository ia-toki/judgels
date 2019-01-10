package org.iatoki.judgels.play.jid;

import org.iatoki.judgels.play.model.Dao;

import java.util.Collection;
import java.util.List;

public interface BaseJidCacheDao<M extends AbstractJidCacheModel> extends Dao<Long, M> {

    M createJidCacheModel();

    boolean existsByJid(String jid);

    M findByJid(String jid);

    List<M> getByJids(Collection<String> jids);
}
