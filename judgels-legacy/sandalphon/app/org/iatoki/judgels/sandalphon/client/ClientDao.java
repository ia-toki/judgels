package org.iatoki.judgels.sandalphon.client;

import com.google.inject.ImplementedBy;
import judgels.persistence.JudgelsDao;

@ImplementedBy(ClientHibernateDao.class)
public interface ClientDao extends JudgelsDao<ClientModel> {

}
