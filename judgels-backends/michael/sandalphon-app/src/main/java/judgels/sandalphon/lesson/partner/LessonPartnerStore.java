package judgels.sandalphon.lesson.partner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.lesson.partner.LessonPartnerConfig;
import judgels.sandalphon.api.resource.Partner;
import judgels.sandalphon.api.resource.PartnerPermission;
import judgels.sandalphon.persistence.LessonPartnerDao;
import judgels.sandalphon.persistence.LessonPartnerModel;

@Singleton
public class LessonPartnerStore {
    private final LessonPartnerDao partnerDao;
    private final ObjectMapper mapper;

    @Inject
    public LessonPartnerStore(LessonPartnerDao partnerDao, ObjectMapper mapper) {
        this.partnerDao = partnerDao;
        this.mapper = mapper;
    }

    public Optional<Partner> getPartner(String lessonJid, String userJid) {
        return partnerDao.selectByLessonJidAndUserJid(lessonJid, userJid).map(this::fromModel);
    }

    public List<Partner> getPartners(String lessonJid) {
        return Lists.transform(partnerDao.selectAllByLessonJid(lessonJid), this::fromModel);
    }

    public void setPartners(String lessonJid, List<Partner> partners) {
        Map<String, Partner> partnersByUserJid = new HashMap<>();
        for (Partner partner : partners) {
            partnersByUserJid.put(partner.getUserJid(), partner);
        }

        for (LessonPartnerModel model : partnerDao.selectAllByLessonJid(lessonJid)) {
            Partner existingPartner = partnersByUserJid.get(model.userJid);
            if (existingPartner == null) {
                partnerDao.delete(model);
            }
        }

        for (Partner partner : partners) {
            upsertPartner(lessonJid, partner);
        }
    }

    private void upsertPartner(String lessonJid, Partner partner) {
        Optional<LessonPartnerModel> maybeModel = partnerDao.selectByLessonJidAndUserJid(lessonJid, partner.getUserJid());
        if (maybeModel.isPresent()) {
            LessonPartnerModel model = maybeModel.get();

            try {
                LessonPartnerConfig config = mapper.readValue(model.config, LessonPartnerConfig.class);
                model.config = mapper.writeValueAsString(new LessonPartnerConfig.Builder()
                        .from(config)
                        .isAllowedToUpdateLesson(partner.getPermission() == PartnerPermission.UPDATE)
                        .build());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            partnerDao.update(model);
        } else {
            LessonPartnerModel model = new LessonPartnerModel();
            model.lessonJid = lessonJid;
            model.userJid = partner.getUserJid();

            boolean isUpdate = partner.getPermission() == PartnerPermission.UPDATE;
            try {
                model.config = mapper.writeValueAsString(new LessonPartnerConfig.Builder()
                        .isAllowedToUpdateLesson(isUpdate)
                        .isAllowedToUpdateStatement(isUpdate)
                        .isAllowedToUploadStatementResources(isUpdate)
                        .isAllowedToManageStatementLanguages(isUpdate)
                        .isAllowedToViewVersionHistory(isUpdate)
                        .isAllowedToRestoreVersionHistory(isUpdate)
                        .build());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            partnerDao.insert(model);
        }
    }

    private Partner fromModel(LessonPartnerModel model) {
        LessonPartnerConfig config;
        try {
            config = mapper.readValue(model.config, LessonPartnerConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Partner.Builder()
                .userJid(model.userJid)
                .permission(config.getIsAllowedToUpdateLesson() ? PartnerPermission.UPDATE : PartnerPermission.VIEW)
                .build();
    }
}
