package org.iatoki.judgels.jerahmeel.scorecache;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

@ImplementedBy(ContainerProblemScoreCacheHibernateDao.class)
public interface ContainerProblemScoreCacheDao extends Dao<Long, ContainerProblemScoreCacheModel> {

    boolean existsByUserJidContainerJidAndProblemJid(String userJid, String containerJid, String problemJid);

    ContainerProblemScoreCacheModel getByUserJidContainerJidAndProblemJid(String userJid, String containerJid, String problemJid);
}
