package org.iatoki.judgels.jerahmeel.scorecache;

import com.google.inject.ImplementedBy;
import judgels.persistence.Dao;

@ImplementedBy(ContainerProblemScoreCacheHibernateDao.class)
public interface ContainerProblemScoreCacheDao extends Dao<ContainerProblemScoreCacheModel> {

    boolean existsByUserJidContainerJidAndProblemJid(String userJid, String containerJid, String problemJid);

    ContainerProblemScoreCacheModel getByUserJidContainerJidAndProblemJid(String userJid, String containerJid, String problemJid);
}
