package org.iatoki.judgels.play.migration;

import com.google.inject.ImplementedBy;

@ImplementedBy(DataVersionHibernateDao.class)
public interface DataVersionDao {

    long getVersion();

    void update(long version);
}
