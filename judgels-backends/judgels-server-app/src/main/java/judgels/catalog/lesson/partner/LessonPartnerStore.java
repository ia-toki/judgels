package judgels.catalog.lesson.partner;

import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import judgels.api.catalog.lesson.partner.LessonPartner;
import judgels.persistence.dao.LessonPartnerDao;
import judgels.persistence.model.LessonPartnerModel;

@Singleton
public class LessonPartnerStore {
    private final LessonPartnerDao partnerDao;

    @Inject
    public LessonPartnerStore(LessonPartnerDao partnerDao) {
        this.partnerDao = partnerDao;
    }

    public boolean isPartner(String lessonJid, String userJid) {
        return partnerDao.selectByLessonJidAndUserJid(lessonJid, userJid).isPresent();
    }

    public List<LessonPartner> getPartners(String lessonJid) {
        return Lists.transform(partnerDao.selectAllByLessonJid(lessonJid), this::fromModel);
    }

    public void setPartners(String lessonJid, Collection<String> userJids) {
        Set<String> newUserJids = new HashSet<>(userJids);

        for (LessonPartnerModel model : partnerDao.selectAllByLessonJid(lessonJid)) {
            if (!newUserJids.remove(model.userJid)) {
                partnerDao.delete(model);
            }
        }

        for (String userJid : newUserJids) {
            LessonPartnerModel model = new LessonPartnerModel();
            model.lessonJid = lessonJid;
            model.userJid = userJid;

            // Every partner can edit the lesson, so this per-partner config is no longer read.
            model.config = "{}";

            partnerDao.insert(model);
        }
    }

    private LessonPartner fromModel(LessonPartnerModel model) {
        return new LessonPartner.Builder()
                .userJid(model.userJid)
                .build();
    }
}
