package org.iatoki.judgels.jerahmeel.archive;

import com.google.inject.ImplementedBy;
import judgels.persistence.JudgelsDao;

@ImplementedBy(ArchiveHibernateDao.class)
public interface ArchiveDao extends JudgelsDao<ArchiveModel> {

}
