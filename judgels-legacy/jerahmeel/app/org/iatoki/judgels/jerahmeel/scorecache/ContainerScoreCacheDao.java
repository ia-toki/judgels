package org.iatoki.judgels.jerahmeel.scorecache;

import com.google.inject.ImplementedBy;
import judgels.persistence.Dao;

@ImplementedBy(ContainerScoreCacheHibernateDao.class)
public interface ContainerScoreCacheDao extends Dao<ContainerScoreCacheModel> {

    boolean existsByUserJidAndContainerJid(String userJid, String containerJid);

    ContainerScoreCacheModel getByUserJidAndContainerJid(String userJid, String containerJid);
}
