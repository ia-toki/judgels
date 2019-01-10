package org.iatoki.judgels.sandalphon.client.problem;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

import java.util.List;

@ImplementedBy(ClientProblemHibernateDao.class)
public interface ClientProblemDao extends Dao<Long, ClientProblemModel> {

    boolean existsByClientJidAndProblemJid(String clientJid, String problemJid);

    ClientProblemModel findByClientJidAndProblemJid(String clientJid, String problemJid);

    List<ClientProblemModel> getByProblemJid(String problemJid);
}
