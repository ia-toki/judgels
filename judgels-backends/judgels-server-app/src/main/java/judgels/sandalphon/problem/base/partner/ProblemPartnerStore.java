package judgels.sandalphon.problem.base.partner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import judgels.sandalphon.api.problem.partner.ProblemPartnerChildConfig;
import judgels.sandalphon.api.problem.partner.ProblemPartnerConfig;
import judgels.sandalphon.api.resource.Partner;
import judgels.sandalphon.api.resource.PartnerPermission;
import judgels.sandalphon.persistence.ProblemPartnerDao;
import judgels.sandalphon.persistence.ProblemPartnerModel;

public class ProblemPartnerStore {
    private final ProblemPartnerDao partnerDao;
    private final ObjectMapper mapper;

    @Inject
    public ProblemPartnerStore(ProblemPartnerDao partnerDao, ObjectMapper mapper) {
        this.partnerDao = partnerDao;
        this.mapper = mapper;
    }

    public Optional<Partner> getPartner(String problemJid, String userJid) {
        return partnerDao.selectByProblemJidAndUserJid(problemJid, userJid).map(this::fromModel);
    }

    public List<Partner> getPartners(String problemJid) {
        return Lists.transform(partnerDao.selectAllByProblemJid(problemJid), this::fromModel);
    }

    public void setPartners(String problemJid, List<Partner> partners) {
        Map<String, Partner> partnersByUserJid = new HashMap<>();
        for (Partner partner : partners) {
            partnersByUserJid.put(partner.getUserJid(), partner);
        }

        for (ProblemPartnerModel model : partnerDao.selectAllByProblemJid(problemJid)) {
            Partner existingPartner = partnersByUserJid.get(model.userJid);
            if (existingPartner == null) {
                partnerDao.delete(model);
            }
        }

        for (Partner partner : partners) {
            upsertPartner(problemJid, partner);
        }
    }

    private void upsertPartner(String problemJid, Partner partner) {
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

    private Partner fromModel(ProblemPartnerModel model) {
        ProblemPartnerConfig config;
        try {
            config = mapper.readValue(model.baseConfig, ProblemPartnerConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Partner.Builder()
                .userJid(model.userJid)
                .permission(config.getIsAllowedToUpdateProblem() ? PartnerPermission.UPDATE : PartnerPermission.VIEW)
                .build();
    }
}
