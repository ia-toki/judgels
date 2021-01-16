package org.iatoki.judgels.sandalphon.jid;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.jid.BaseJidCacheDao;

@ImplementedBy(JidCacheHibernateDao.class)
public interface JidCacheDao extends BaseJidCacheDao<JidCacheModel> {

}
