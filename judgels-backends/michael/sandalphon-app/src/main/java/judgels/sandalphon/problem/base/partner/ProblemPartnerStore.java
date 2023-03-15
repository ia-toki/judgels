package judgels.sandalphon.problem.base.partner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.partner.PartnerPermission;
import judgels.sandalphon.api.problem.partner.ProblemPartnerChildConfig;
import judgels.sandalphon.api.problem.partner.ProblemPartnerConfig;
import judgels.sandalphon.api.problem.partner.ProblemPartnerV2;
import judgels.sandalphon.persistence.ProblemPartnerDao;
import judgels.sandalphon.persistence.ProblemPartnerModel;

@Singleton
public class ProblemPartnerStore {
    private final ProblemPartnerDao partnerDao;
    private final ObjectMapper mapper;

    @Inject
    public ProblemPartnerStore(ProblemPartnerDao partnerDao, ObjectMapper mapper) {
        this.partnerDao = partnerDao;
        this.mapper = mapper;
    }

    public List<ProblemPartnerV2> getPartners(String problemJid) {
        return Lists.transform(partnerDao.selectAllByProblemJid(problemJid), this::fromModel);
    }

    public void setPartners(String problemJid, List<ProblemPartnerV2> partners) {
        Map<String, ProblemPartnerV2> partnersByUserJid = new HashMap<>();
        for (ProblemPartnerV2 partner : partners) {
            partnersByUserJid.put(partner.getUserJid(), partner);
        }

        for (ProblemPartnerModel model : partnerDao.selectAllByProblemJid(problemJid)) {
            ProblemPartnerV2 existingPartner = partnersByUserJid.get(model.userJid);
            if (existingPartner == null) {
                partnerDao.delete(model);
            }
        }

        for (ProblemPartnerV2 partner : partners) {
            upsertPartner(problemJid, partner);
        }
    }

    private void upsertPartner(String problemJid, ProblemPartnerV2 partner) {
        Optional<ProblemPartnerModel> maybeModel = partnerDao.selectByProblemJidAndUserJid(problemJid, partner.getUserJid());
        if (maybeModel.isPresent()) {
            ProblemPartnerModel model = maybeModel.get();

            try {
                ProblemPartnerConfig config = mapper.readValue(model.baseConfig, ProblemPartnerConfig.class);
                model.baseConfig = mapper.writeValueAsString(new ProblemPartnerConfig.Builder()
                        .from(config)
                        .isAllowedToUpdateProblem(partner.getPermission() == PartnerPermission.UPDATE)
                        .build());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            partnerDao.update(model);
        } else {
            ProblemPartnerModel model = new ProblemPartnerModel();
            model.problemJid = problemJid;
            model.userJid = partner.getUserJid();

            boolean isUpdate = partner.getPermission() == PartnerPermission.UPDATE;
            try {
                model.baseConfig = mapper.writeValueAsString(new ProblemPartnerConfig.Builder()
                        .isAllowedToUpdateProblem(isUpdate)
                        .isAllowedToUpdateStatement(isUpdate)
                        .isAllowedToUploadStatementResources(isUpdate)
                        .isAllowedToManageStatementLanguages(isUpdate)
                        .isAllowedToViewVersionHistory(isUpdate)
                        .isAllowedToRestoreVersionHistory(isUpdate)
                        .build());
                model.childConfig = mapper.writeValueAsString(new ProblemPartnerChildConfig.Builder()
                        .isAllowedToSubmit(isUpdate)
                        .isAllowedToManageGrading(isUpdate)
                        .isAllowedToManageItems(isUpdate)
                        .build());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            partnerDao.insert(model);
        }
    }

    private ProblemPartnerV2 fromModel(ProblemPartnerModel model) {
        ProblemPartnerConfig config;
        try {
            config = mapper.readValue(model.baseConfig, ProblemPartnerConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ProblemPartnerV2.Builder()
                .userJid(model.userJid)
                .permission(config.getIsAllowedToUpdateProblem() ? PartnerPermission.UPDATE : PartnerPermission.VIEW)
                .build();
    }
}
