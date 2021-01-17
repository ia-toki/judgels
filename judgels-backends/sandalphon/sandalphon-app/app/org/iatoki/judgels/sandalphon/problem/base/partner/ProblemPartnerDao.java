package org.iatoki.judgels.sandalphon.problem.base.partner;

import com.google.inject.ImplementedBy;
import java.util.List;
import judgels.persistence.Dao;

@ImplementedBy(ProblemPartnerHibernateDao.class)
public interface ProblemPartnerDao extends Dao<ProblemPartnerModel> {

    boolean existsByProblemJidAndPartnerJid(String problemJid, String partnerJid);

    ProblemPartnerModel findByProblemJidAndPartnerJid(String problemJid, String partnerJid);

    List<String> getProblemJidsByPartnerJid(String partnerJid);
}
