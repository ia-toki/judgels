package judgels.sandalphon.lesson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.Git;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.partner.LessonPartner;
import judgels.sandalphon.api.lesson.partner.LessonPartnerConfig;
import judgels.sandalphon.persistence.LessonDao;
import judgels.sandalphon.persistence.LessonModel;
import judgels.sandalphon.persistence.LessonModel_;
import judgels.sandalphon.persistence.LessonPartnerDao;
import judgels.sandalphon.persistence.LessonPartnerModel;
import judgels.sandalphon.persistence.LessonPartnerModel_;

public final class LessonStore extends BaseLessonStore {
    private final ObjectMapper mapper;
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
        this.mapper = mapper;
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

    public void createLessonPartner(String lessonJid, String userJid, LessonPartnerConfig config) {
        LessonModel model = lessonDao.findByJid(lessonJid);

        LessonPartnerModel partnerModel = new LessonPartnerModel();
        partnerModel.lessonJid = model.jid;
        partnerModel.userJid = userJid;
        partnerModel.config = writeObj(config);

        lessonPartnerDao.insert(partnerModel);
        lessonDao.update(model);
    }

    public void updateLessonPartner(long lessonPartnerId, LessonPartnerConfig config) {
        LessonPartnerModel partnerModel = lessonPartnerDao.find(lessonPartnerId);
        partnerModel.config = writeObj(config);

        lessonPartnerDao.update(partnerModel);

        LessonModel model = lessonDao.findByJid(partnerModel.lessonJid);
        lessonDao.update(model);
    }

    public Page<LessonPartner> getPageOfLessonPartners(String lessonJid, long pageIndex, String orderBy, String orderDir) {
        FilterOptions<LessonPartnerModel> filterOptions = new FilterOptions.Builder<LessonPartnerModel>()
                .putColumnsEq(LessonPartnerModel_.lessonJid, lessonJid)
                .build();
        SelectionOptions selectionOptions = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .page((int) pageIndex)
                .orderBy(orderBy)
                .orderDir(OrderDir.of(orderDir))
                .build();

        long totalCount = lessonPartnerDao.selectCount(filterOptions);
        List<LessonPartnerModel> models = lessonPartnerDao.selectAll(filterOptions, selectionOptions);
        List<LessonPartner> partners = Lists.transform(models, this::createLessonPartnerFromModel);

        return new Page.Builder<LessonPartner>()
                .page(partners)
                .totalCount(totalCount)
                .pageIndex(selectionOptions.getPage())
                .pageSize(selectionOptions.getPageSize())
                .build();
    }

    public Optional<LessonPartner> findLessonPartnerById(long lessonPartnerId) {
        return lessonPartnerDao.select(lessonPartnerId).map(this::createLessonPartnerFromModel);
    }

    public LessonPartner findLessonPartnerByLessonJidAndPartnerJid(String lessonJid, String partnerJid) {
        LessonPartnerModel lessonPartnerModel = lessonPartnerDao.findByLessonJidAndPartnerJid(lessonJid, partnerJid);

        return createLessonPartnerFromModel(lessonPartnerModel);
    }

    public void updateLesson(String lessonJid, String slug, String additionalNote) {
        LessonModel lessonModel = lessonDao.findByJid(lessonJid);
        lessonModel.slug = slug;
        lessonModel.additionalNote = additionalNote;

        lessonDao.update(lessonModel);
    }

    public Page<Lesson> getPageOfLessons(long pageIndex, String orderBy, String orderDir, String filterString, String userJid, boolean isAdmin) {
        FilterOptions<LessonModel> filterOptions;
        SelectionOptions selectionOptions = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .page((int) pageIndex)
                .orderBy(orderBy)
                .orderDir(OrderDir.of(orderDir))
                .build();

        if (isAdmin) {
            filterOptions = new FilterOptions.Builder<LessonModel>()
                    .putColumnsLike(LessonModel_.slug, filterString)
                    .putColumnsLike(LessonModel_.additionalNote, filterString)
                    .build();
        } else {
            List<String> lessonJidsWhereIsAuthor = lessonDao.getJidsByAuthorJid(userJid);
            List<String> lessonJidsWhereIsPartner = lessonPartnerDao.getLessonJidsByPartnerJid(userJid);

            ImmutableSet.Builder<String> allowedLessonJidsBuilder = ImmutableSet.builder();
            allowedLessonJidsBuilder.addAll(lessonJidsWhereIsAuthor);
            allowedLessonJidsBuilder.addAll(lessonJidsWhereIsPartner);

            Set<String> allowedLessonJids = allowedLessonJidsBuilder.build();

            filterOptions = new FilterOptions.Builder<LessonModel>()
                    .putColumnsIn(LessonModel_.jid, allowedLessonJids)
                    .putColumnsLike(LessonModel_.slug, filterString)
                    .putColumnsLike(LessonModel_.additionalNote, filterString)
                    .build();
        }

        long totalCount = lessonDao.selectCount(filterOptions);
        List<LessonModel> models = lessonDao.selectAll(filterOptions, selectionOptions);

        List<Lesson> lessons = Lists.transform(models, LessonStore::createLessonFromModel);
        return new Page.Builder<Lesson>()
                .page(lessons)
                .totalCount(totalCount)
                .pageIndex(selectionOptions.getPage())
                .pageSize(selectionOptions.getPageSize())
                .build();
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

    private LessonPartner createLessonPartnerFromModel(LessonPartnerModel lessonPartnerModel) {
        try {
            return new LessonPartner.Builder()
                    .id(lessonPartnerModel.id)
                    .lessonJid(lessonPartnerModel.lessonJid)
                    .userJid(lessonPartnerModel.userJid)
                    .config(mapper.readValue(lessonPartnerModel.config, LessonPartnerConfig.class))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
