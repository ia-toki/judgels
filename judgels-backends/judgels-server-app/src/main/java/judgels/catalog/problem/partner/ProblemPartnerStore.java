package judgels.catalog.problem.partner;

import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import judgels.api.catalog.problem.partner.ProblemPartner;
import judgels.persistence.dao.ProblemPartnerDao;
import judgels.persistence.model.ProblemPartnerModel;

public class ProblemPartnerStore {
    private final ProblemPartnerDao partnerDao;

    @Inject
    public ProblemPartnerStore(ProblemPartnerDao partnerDao) {
        this.partnerDao = partnerDao;
    }

    public boolean isPartner(String problemJid, String userJid) {
        return partnerDao.selectByProblemJidAndUserJid(problemJid, userJid).isPresent();
    }

    public List<ProblemPartner> getPartners(String problemJid) {
        return Lists.transform(partnerDao.selectAllByProblemJid(problemJid), this::fromModel);
    }

    public void setPartners(String problemJid, Collection<String> userJids) {
        Set<String> newUserJids = new HashSet<>(userJids);

        for (ProblemPartnerModel model : partnerDao.selectAllByProblemJid(problemJid)) {
            if (!newUserJids.remove(model.userJid)) {
                partnerDao.delete(model);
            }
        }

        for (String userJid : newUserJids) {
            ProblemPartnerModel model = new ProblemPartnerModel();
            model.problemJid = problemJid;
            model.userJid = userJid;

            // Every partner can edit the problem, so these per-partner configs are no longer read.
            model.baseConfig = "{}";
            model.childConfig = "{}";

            partnerDao.insert(model);
        }
    }

    private ProblemPartner fromModel(ProblemPartnerModel model) {
        return new ProblemPartner.Builder()
                .userJid(model.userJid)
                .build();
    }
}
