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
    private final LessonPartnerDao lessonPartnerDao;

    @Inject
    public LessonStore(
            ObjectMapper mapper,
            @LessonFs FileSystem lessonFs,
            @LessonGit Git lessonGit,
            LessonDao lessonDao,
            LessonPartnerDao lessonPartnerDao) {

        super(mapper, lessonFs);
        this.lessonGit = lessonGit;
        this.lessonDao = lessonDao;
        this.lessonPartnerDao = lessonPartnerDao;
    }

    public Lesson createLesson(String slug, String additionalNote) {
        LessonModel model = new LessonModel();
        model.slug = slug;
        model.additionalNote = additionalNote;

        lessonDao.insert(model);
        lessonFs.createDirectory(getClonesDirPath(model.jid));

        return createLessonFromModel(model);
    }

    public boolean lessonExistsByJid(String lessonJid) {
        return lessonDao.existsByJid(lessonJid);
    }

    public boolean lessonExistsBySlug(String slug) {
        return lessonDao.existsBySlug(slug);
    }

    public Optional<Lesson> findLessonById(long lessonId) {
        return lessonDao.select(lessonId).map(m -> createLessonFromModel(m));
    }

    public Lesson findLessonByJid(String lessonJid) {
        LessonModel model = lessonDao.findByJid(lessonJid);
        return createLessonFromModel(model);
    }

    public Lesson findLessonBySlug(String slug) {
        LessonModel model = lessonDao.findBySlug(slug);
        return createLessonFromModel(model);
    }

    public boolean isUserPartnerForLesson(String lessonJid, String userJid) {
        return lessonPartnerDao.existsByLessonJidAndPartnerJid(lessonJid, userJid);
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
        return models.mapPage(p -> Lists.transform(p, LessonStore::createLessonFromModel));
    }

    public void updateLesson(String lessonJid, String slug, String additionalNote) {
        LessonModel lessonModel = lessonDao.findByJid(lessonJid);
        lessonModel.slug = slug;
        lessonModel.additionalNote = additionalNote;

        lessonDao.update(lessonModel);
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

    private static  Lesson createLessonFromModel(LessonModel lessonModel) {
        return new Lesson.Builder()
                .id(lessonModel.id)
                .jid(lessonModel.jid)
                .slug(lessonModel.slug)
                .authorJid(lessonModel.createdBy)
                .additionalNote(lessonModel.additionalNote)
                .lastUpdateTime(lessonModel.createdAt)
                .build();
    }
}
