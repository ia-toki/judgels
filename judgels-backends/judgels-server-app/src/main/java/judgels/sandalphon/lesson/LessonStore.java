package judgels.sandalphon.lesson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.nio.file.Path;
import java.util.Optional;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.Git;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.persistence.LessonDao;
import judgels.sandalphon.persistence.LessonModel;
import judgels.sandalphon.persistence.LessonPartnerDao;

public final class LessonStore extends BaseLessonStore {
    private final Git lessonGit;
    private final LessonDao lessonDao;
    private final LessonPartnerDao partnerDao;

    @Inject
    public LessonStore(
            ObjectMapper mapper,
            @LessonFs FileSystem lessonFs,
            @LessonGit Git lessonGit,
            LessonDao lessonDao,
            LessonPartnerDao partnerDao) {

        super(mapper, lessonFs);
        this.lessonGit = lessonGit;
        this.lessonDao = lessonDao;
        this.partnerDao = partnerDao;
    }

    public Lesson createLesson(String slug, String additionalNote) {
        LessonModel model = new LessonModel();
        model.slug = slug;
        model.additionalNote = additionalNote;

        lessonDao.insert(model);
        lessonFs.createDirectory(getClonesDirPath(model.jid));

        return fromModel(model);
    }

    public boolean lessonExistsByJid(String lessonJid) {
        return lessonDao.existsByJid(lessonJid);
    }

    public boolean lessonExistsBySlug(String slug) {
        return lessonDao.selectBySlug(slug).isPresent();
    }

    public Optional<Lesson> getLessonById(long lessonId) {
        return lessonDao.select(lessonId).map(LessonStore::fromModel);
    }

    public Optional<Lesson> getLessonByJid(String lessonJid) {
        return lessonDao.selectByJid(lessonJid).map(LessonStore::fromModel);
    }

    public Optional<Lesson> getLessonBySlug(String slug) {
        return lessonDao.selectBySlug(slug).map(LessonStore::fromModel);
    }

    public boolean isUserPartnerForLesson(String lessonJid, String userJid) {
        return partnerDao.selectByLessonJidAndUserJid(lessonJid, userJid).isPresent();
    }

    public Page<Lesson> getLessons(String userJid, boolean isAdmin, String termFilter, int pageIndex) {
        SelectionOptions selectionOptions = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .orderBy("updatedAt")
                .page(pageIndex)
                .build();

        Page<LessonModel> models = isAdmin
                ? lessonDao.selectPaged(termFilter, selectionOptions)
                : lessonDao.selectPagedByUserJid(userJid, termFilter, selectionOptions);
        return models.mapPage(p -> Lists.transform(p, LessonStore::fromModel));
    }

    public void updateLesson(String lessonJid, String slug, String additionalNote) {
        LessonModel model = lessonDao.findByJid(lessonJid);
        model.slug = slug;
        model.additionalNote = additionalNote;

        lessonDao.update(model);
    }

    public void initRepository(String userJid, String lessonJid) {
        Path root = getRootDirPath(lessonFs, null, lessonJid);

        lessonGit.init(root);
        lessonGit.addAll(root);
        lessonGit.commit(root, userJid, "no@email.com", "Initial commit", "");
    }

    public boolean userCloneExists(String userJid, String lessonJid) {
        Path root = getCloneDirPath(userJid, lessonJid);

        return lessonFs.directoryExists(root);
    }

    public void createUserCloneIfNotExists(String userJid, String lessonJid) {
        Path origin = getOriginDirPath(lessonJid);
        Path root = getCloneDirPath(userJid, lessonJid);

        if (!lessonFs.directoryExists(root)) {
            lessonGit.clone(origin, root);
        }
    }

    private static Lesson fromModel(LessonModel model) {
        return new Lesson.Builder()
                .id(model.id)
                .jid(model.jid)
                .slug(model.slug)
                .authorJid(model.createdBy)
                .additionalNote(model.additionalNote)
                .lastUpdateTime(model.createdAt)
                .build();
    }
}
