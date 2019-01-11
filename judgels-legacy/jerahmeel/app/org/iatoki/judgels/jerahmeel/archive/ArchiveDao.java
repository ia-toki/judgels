package org.iatoki.judgels.jerahmeel.archive;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

@ImplementedBy(ArchiveHibernateDao.class)
public interface ArchiveDao extends JudgelsDao<ArchiveModel> {

}
