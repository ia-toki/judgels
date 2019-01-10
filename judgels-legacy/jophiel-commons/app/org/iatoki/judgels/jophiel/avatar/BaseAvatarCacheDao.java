package org.iatoki.judgels.jophiel.avatar;

import org.iatoki.judgels.play.model.Dao;

import java.util.List;

public interface BaseAvatarCacheDao<M extends AbstractAvatarCacheModel> extends Dao<Long, M> {

    M createAvatarCacheModel();

    boolean existsByUserJid(String userJid);

    M findByUserJid(String userJid);

    List<M> findByUserJids(List<String> userJids);
}
