package org.iatoki.judgels.sandalphon.client;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.JudgelsDao;

@ImplementedBy(ClientHibernateDao.class)
public interface ClientDao extends JudgelsDao<ClientModel> {

}
