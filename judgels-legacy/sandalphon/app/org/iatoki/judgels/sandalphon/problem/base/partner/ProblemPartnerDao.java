package org.iatoki.judgels.sandalphon.problem.base.partner;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

import java.util.List;

@ImplementedBy(ProblemPartnerHibernateDao.class)
public interface ProblemPartnerDao extends Dao<Long, ProblemPartnerModel> {

    boolean existsByProblemJidAndPartnerJid(String problemJid, String partnerJid);

    ProblemPartnerModel findByProblemJidAndPartnerJid(String problemJid, String partnerJid);

    List<String> getProblemJidsByPartnerJid(String partnerJid);
}
