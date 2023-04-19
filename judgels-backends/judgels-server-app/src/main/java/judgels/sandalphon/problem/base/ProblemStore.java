package judgels.sandalphon.problem.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.persistence.FilterOptions;
import judgels.persistence.JidGenerator;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.Git;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemSetterRole;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.api.problem.partner.ProblemPartner;
import judgels.sandalphon.api.problem.partner.ProblemPartnerChildConfig;
import judgels.sandalphon.api.problem.partner.ProblemPartnerConfig;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemModel;
import judgels.sandalphon.persistence.ProblemPartnerDao;
import judgels.sandalphon.persistence.ProblemPartnerModel;
import judgels.sandalphon.persistence.ProblemPartnerModel_;
import judgels.sandalphon.persistence.ProblemSetterDao;
import judgels.sandalphon.persistence.ProblemSetterModel;

public class ProblemStore extends BaseProblemStore {
    private final Git problemGit;
    private final ProblemDao problemDao;
    private final ProblemSetterDao setterDao;
    private final ProblemPartnerDao partnerDao;

    @Inject
    public ProblemStore(
            ObjectMapper mapper,
            @ProblemFs FileSystem problemFs,
            @ProblemGit Git problemGit,
            ProblemDao problemDao,
            ProblemSetterDao setterDao,
            ProblemPartnerDao partnerDao) {

        super(mapper, problemFs);
        this.problemGit = problemGit;
        this.problemDao = problemDao;
        this.setterDao = setterDao;
        this.partnerDao = partnerDao;
    }

    public Problem createProblem(ProblemType type, String slug, String additionalNote) {
        ProblemModel model = new ProblemModel();
        model.slug = slug;
        model.additionalNote = additionalNote;

        problemDao.insertWithJid(JidGenerator.newChildJid(ProblemModel.class, type.ordinal()), model);
        problemFs.createDirectory(getClonesDirPath(model.jid));

        return createProblemFromModel(model);
    }

    public boolean problemExistsByJid(String problemJid) {
        return problemDao.existsByJid(problemJid);
    }

    public boolean problemExistsBySlug(String slug) {
        return problemDao.existsBySlug(slug);
    }

    public Optional<Problem> findProblemById(long problemId) {
        return problemDao.select(problemId).map(ProblemStore::createProblemFromModel);
    }

    public Problem findProblemByJid(String problemJid) {
        ProblemModel model = problemDao.findByJid(problemJid);
        return createProblemFromModel(model);
    }

    public Problem findProblemBySlug(String slug) {
        ProblemModel model = problemDao.findBySlug(slug);
        return createProblemFromModel(model);
    }

    public Map<ProblemSetterRole, List<String>> findProblemSettersByProblemJid(String problemJid) {
        Map<ProblemSetterRole, List<String>> setters = Maps.newHashMap();
        for (ProblemSetterModel m : setterDao.selectAllByProblemJid(problemJid)) {
            ProblemSetterRole role = ProblemSetterRole.valueOf(m.role);
            setters.putIfAbsent(role, Lists.newArrayList());
            setters.get(role).add(m.userJid);
        }
        return setters;
    }

    public void updateProblemSettersByProblemJidAndRole(String problemJid, ProblemSetterRole role, List<String> userJids) {
        setterDao.selectAllByProblemJidAndRole(problemJid, role).forEach(setterDao::delete);
        setterDao.flush();

        for (String userJid : userJids) {
            ProblemSetterModel m = new ProblemSetterModel();
            m.problemJid = problemJid;
            m.userJid = userJid;
            m.role = role.name();
            setterDao.insert(m);
        }
    }

    public boolean isUserPartnerForProblem(String problemJid, String userJid) {
        return partnerDao.existsByProblemJidAndPartnerJid(problemJid, userJid);
    }

    public void createProblemPartner(String problemJid, String userJid, ProblemPartnerConfig baseConfig, ProblemPartnerChildConfig childConfig) {
        ProblemModel model = problemDao.findByJid(problemJid);

        ProblemPartnerModel partnerModel = new ProblemPartnerModel();
        partnerModel.problemJid = model.jid;
        partnerModel.userJid = userJid;

        try {
            partnerModel.baseConfig = mapper.writeValueAsString(baseConfig);
            partnerModel.childConfig = mapper.writeValueAsString(childConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        partnerDao.insert(partnerModel);
        problemDao.update(model);
    }

    public void updateProblemPartner(long problemPartnerId, ProblemPartnerConfig baseConfig, ProblemPartnerChildConfig childConfig) {
        ProblemPartnerModel partnerModel = partnerDao.find(problemPartnerId);

        try {
            partnerModel.baseConfig = mapper.writeValueAsString(baseConfig);
            partnerModel.childConfig = mapper.writeValueAsString(childConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        partnerDao.update(partnerModel);

        ProblemModel model = problemDao.findByJid(partnerModel.problemJid);
        problemDao.update(model);
    }

    public Page<ProblemPartner> getPageOfProblemPartners(String problemJid, long pageIndex, String orderBy, String orderDir) {
        FilterOptions<ProblemPartnerModel> filterOptions = new FilterOptions.Builder<ProblemPartnerModel>()
                .putColumnsEq(ProblemPartnerModel_.problemJid, problemJid)
                .build();
        SelectionOptions selectionOptions = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .page((int) pageIndex)
                .orderBy(orderBy)
                .orderDir(OrderDir.of(orderDir))
                .build();

        long totalCount = partnerDao.selectCount(filterOptions);
        List<ProblemPartnerModel> models = partnerDao.selectAll(filterOptions, selectionOptions);
        List<ProblemPartner> partners = Lists.transform(models, this::createProblemPartnerFromModel);

        return new Page.Builder<ProblemPartner>()
                .page(partners)
                .totalCount(totalCount)
                .pageIndex(selectionOptions.getPage())
                .pageSize(selectionOptions.getPageSize())
                .build();
    }

    public Optional<ProblemPartner> findProblemPartnerById(long problemPartnerId) {
        return partnerDao.select(problemPartnerId).map(this::createProblemPartnerFromModel);
    }

    public ProblemPartner findProblemPartnerByProblemJidAndPartnerJid(String problemJid, String partnerJid) {
        ProblemPartnerModel model = partnerDao.findByProblemJidAndPartnerJid(problemJid, partnerJid);
        return createProblemPartnerFromModel(model);
    }

    public void updateProblem(String problemJid, String slug, String additionalNote) {
        ProblemModel model = problemDao.findByJid(problemJid);
        model.slug = slug;
        model.additionalNote = additionalNote;

        problemDao.update(model);
    }

    public void initRepository(String userJid, String problemJid) {
        Path root = getRootDirPath(null, problemJid);

        problemGit.init(root);
        problemGit.addAll(root);
        problemGit.commit(root, userJid, "no@email.com", "Initial commit", "");
    }

    public boolean userCloneExists(String userJid, String problemJid) {
        Path root = getCloneDirPath(userJid, problemJid);

        return problemFs.directoryExists(root);
    }

    public void createUserCloneIfNotExists(String userJid, String problemJid) {
        Path origin = getOriginDirPath(problemJid);
        Path root = getCloneDirPath(userJid, problemJid);

        if (!problemFs.directoryExists(root)) {
            problemGit.clone(origin, root);
        }
    }

    private static ProblemType getProblemType(ProblemModel model) {
        if (model.jid.startsWith("JIDPROG")) {
            return ProblemType.PROGRAMMING;
        } else if (model.jid.startsWith("JIDBUND")) {
            return ProblemType.BUNDLE;
        } else {
            throw new IllegalStateException("Unknown problem type: " + model.jid);
        }
    }

    public static Problem createProblemFromModel(ProblemModel model) {
        return new Problem.Builder()
                .id(model.id)
                .jid(model.jid)
                .slug(model.slug)
                .additionalNote(model.additionalNote)
                .authorJid(model.createdBy)
                .lastUpdateTime(model.updatedAt)
                .type(getProblemType(model))
                .build();
    }

    private ProblemPartner createProblemPartnerFromModel(ProblemPartnerModel model) {
        try {
            return new ProblemPartner.Builder()
                    .id(model.id)
                    .problemJid(model.problemJid)
                    .userJid(model.userJid)
                    .baseConfig(mapper.readValue(model.baseConfig, ProblemPartnerConfig.class))
                    .childConfig(mapper.readValue(model.childConfig, ProblemPartnerChildConfig.class))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
