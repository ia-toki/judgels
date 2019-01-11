package org.iatoki.judgels.jerahmeel.scorecache;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

@ImplementedBy(ContainerScoreCacheHibernateDao.class)
public interface ContainerScoreCacheDao extends Dao<Long, ContainerScoreCacheModel> {

    boolean existsByUserJidAndContainerJid(String userJid, String containerJid);

    ContainerScoreCacheModel getByUserJidAndContainerJid(String userJid, String containerJid);
}
