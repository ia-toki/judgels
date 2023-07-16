package judgels.sandalphon.problem.base;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.persistence.JidGenerator;
import judgels.persistence.Model_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.sandalphon.Git;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemSetterRole;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.persistence.ProblemDao.ProblemQueryBuilder;
import judgels.sandalphon.persistence.ProblemModel;
import judgels.sandalphon.persistence.ProblemPartnerDao;
import judgels.sandalphon.persistence.ProblemSetterDao;
import judgels.sandalphon.persistence.ProblemSetterModel;
import judgels.sandalphon.problem.base.tag.ProblemTags;

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

        return fromModel(model);
    }

    public boolean problemExistsByJid(String problemJid) {
        return problemDao.existsByJid(problemJid);
    }

    public boolean problemExistsBySlug(String slug) {
        return problemDao.selectBySlug(slug).isPresent();
    }

    public Optional<Problem> getProblemById(long problemId) {
        return problemDao.selectById(problemId).map(ProblemStore::fromModel);
    }

    public Optional<Problem> getProblemByJid(String problemJid) {
        return problemDao.selectByJid(problemJid).map(ProblemStore::fromModel);
    }

    public Optional<Problem> getProblemBySlug(String slug) {
        return problemDao.selectBySlug(slug).map(ProblemStore::fromModel);
    }

    public Page<Problem> getProblems(Optional<String> userJid, String termFilter, Set<String> tagsFilter, int pageNumber, int pageSize) {
        ProblemQueryBuilder query = problemDao.select();

        if (userJid.isPresent()) {
            query.whereUserCanView(userJid.get());
        }
        if (!termFilter.isEmpty()) {
            query.whereTermsMatch(termFilter);
        }
        if (!tagsFilter.isEmpty()) {
            query.whereTagsMatch(ProblemTags.splitTagsFilterByType(tagsFilter));
        }

        return query
                .orderBy(Model_.UPDATED_AT, OrderDir.DESC)
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, ProblemStore::fromModel));
    }

    public Map<ProblemSetterRole, List<String>> getProblemSetters(String problemJid) {
        Map<ProblemSetterRole, List<String>> setters = new HashMap<>();
        for (ProblemSetterModel m : setterDao.selectAllByProblemJid(problemJid)) {
            ProblemSetterRole role = ProblemSetterRole.valueOf(m.role);
            setters.putIfAbsent(role, new ArrayList<>());
            setters.get(role).add(m.userJid);
        }
        return setters;
    }

    public void updateProblemSetters(String problemJid, ProblemSetterRole role, List<String> userJids) {
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
        return partnerDao.selectByProblemJidAndUserJid(problemJid, userJid).isPresent();
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

    public Map<String, String> translateAllowedSlugsToJids(Optional<String> userJid, Set<String> slugs) {
        ProblemQueryBuilder query = problemDao
                .select()
                .whereSlugIn(slugs);

        if (userJid.isPresent()) {
            query.whereUserCanView(userJid.get());
        }

        return query.all().stream().collect(toMap(m -> m.slug, m -> m.jid));
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

    private static Problem fromModel(ProblemModel model) {
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
}
