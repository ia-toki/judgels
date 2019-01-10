package org.iatoki.judgels.jophiel.activity;

import org.iatoki.judgels.play.model.Dao;

public interface BaseActivityLogDao<M extends AbstractActivityLogModel> extends Dao<Long, M> {

    M createActivityLogModel();
}
