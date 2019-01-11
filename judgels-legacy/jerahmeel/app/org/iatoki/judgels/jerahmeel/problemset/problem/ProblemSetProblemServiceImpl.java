package org.iatoki.judgels.jerahmeel.problemset.problem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.iatoki.judgels.jerahmeel.scorecache.ProblemSetScoreCacheUtils;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public final class ProblemSetProblemServiceImpl implements ProblemSetProblemService {

    private final ProblemSetProblemDao problemSetProblemDao;

    @Inject
    public ProblemSetProblemServiceImpl(ProblemSetProblemDao problemSetProblemDao) {
        this.problemSetProblemDao = problemSetProblemDao;
    }

    @Override
    public boolean aliasExistsInProblemSet(String problemSetJid, String alias) {
        return problemSetProblemDao.existsByProblemSetJidAndAlias(problemSetJid, alias);
    }

    @Override
    public ProblemSetProblem findProblemSetProblemById(long problemSetProblemId) throws ProblemSetProblemNotFoundException {
        ProblemSetProblemModel problemSetProblemModel = problemSetProblemDao.findById(problemSetProblemId);
        if (problemSetProblemModel != null) {
            return ProblemSetProblemServiceUtils.createFromModel(problemSetProblemModel);
        } else {
            throw new ProblemSetProblemNotFoundException("ProblemSet Problem Not Found");
        }
    }

    @Override
    public Page<ProblemSetProblem> getPageOfProblemSetProblems(String problemSetJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = problemSetProblemDao.countByFiltersEq(filterString, ImmutableMap.of(ProblemSetProblemModel_.problemSetJid, problemSetJid));
        List<ProblemSetProblemModel> problemSetProblemModels = problemSetProblemDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(ProblemSetProblemModel_.problemSetJid, problemSetJid), pageIndex * pageSize, pageSize);

        List<ProblemSetProblem> problemSetProblems = problemSetProblemModels.stream().map(m -> ProblemSetProblemServiceUtils.createFromModel(m)).collect(Collectors.toList());

        return new Page<>(problemSetProblems, totalPages, pageIndex, pageSize);

    }

    @Override
    public Page<ProblemSetProblemWithScore> getPageOfProblemSetProblemsWithScore(String userJid, String problemSetJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = problemSetProblemDao.countByFiltersEq(filterString, ImmutableMap.of(ProblemSetProblemModel_.problemSetJid, problemSetJid, ProblemSetProblemModel_.status, ProblemSetProblemStatus.VISIBLE.name()));
        List<ProblemSetProblemModel> problemSetProblemModels = problemSetProblemDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(ProblemSetProblemModel_.problemSetJid, problemSetJid, ProblemSetProblemModel_.status, ProblemSetProblemStatus.VISIBLE.name()), pageIndex * pageSize, pageSize);

        ImmutableList.Builder<ProblemSetProblemWithScore> problemSetProblemProgressBuilder = ImmutableList.builder();
        for (ProblemSetProblemModel problemSetProblemModel : problemSetProblemModels) {
            double maxScore = ProblemSetScoreCacheUtils.getInstance().getUserMaxScoreFromProblemSetProblemModel(userJid, problemSetProblemModel);

            problemSetProblemProgressBuilder.add(new ProblemSetProblemWithScore(ProblemSetProblemServiceUtils.createFromModel(problemSetProblemModel), maxScore));
        }

        return new Page<>(problemSetProblemProgressBuilder.build(), totalPages, pageIndex, pageSize);
    }

    @Override
    public void addProblemSetProblem(String problemSetJid, String problemJid, String problemSecret, String alias, ProblemSetProblemType type, ProblemSetProblemStatus status, String userJid, String userIpAddress) {
        ProblemSetProblemModel problemSetProblemModel = new ProblemSetProblemModel();
        problemSetProblemModel.problemSetJid = problemSetJid;
        problemSetProblemModel.problemJid = problemJid;
        problemSetProblemModel.problemSecret = problemSecret;
        problemSetProblemModel.alias = alias;
        problemSetProblemModel.type = type.name();
        problemSetProblemModel.status = status.name();

        problemSetProblemDao.persist(problemSetProblemModel, userJid, userIpAddress);
    }

    @Override
    public void updateProblemSetProblem(long problemSetProblemId, String alias, ProblemSetProblemStatus status, String userJid, String userIpAddress) {
        ProblemSetProblemModel problemSetProblemModel = problemSetProblemDao.findById(problemSetProblemId);
        problemSetProblemModel.alias = alias;
        problemSetProblemModel.status = status.name();

        problemSetProblemDao.edit(problemSetProblemModel, userJid, userIpAddress);
    }

    @Override
    public void removeProblemSetProblem(long problemSetProblemId) {
        ProblemSetProblemModel problemSetProblemModel = problemSetProblemDao.findById(problemSetProblemId);

        problemSetProblemDao.remove(problemSetProblemModel);
    }

    @Override
    public Map<String, String> getProgrammingProblemJidToAliasMapByProblemSetJid(String problemSetJid) {
        List<ProblemSetProblemModel> problemSetProblemModels = problemSetProblemDao.getByProblemSetJid(problemSetJid);

        Map<String, String> map = Maps.newLinkedHashMap();

        for (ProblemSetProblemModel problemSetProblemModel : problemSetProblemModels) {
            if (ProblemSetProblemType.PROGRAMMING.name().equals(problemSetProblemModel.type)) {
                map.put(problemSetProblemModel.problemJid, problemSetProblemModel.alias);
            }
        }

        return map;
    }

    @Override
    public Map<String, String> getBundleProblemJidToAliasMapByProblemSetJid(String problemSetJid) {
        List<ProblemSetProblemModel> problemSetProblemModels = problemSetProblemDao.getByProblemSetJid(problemSetJid);

        Map<String, String> map = Maps.newLinkedHashMap();

        for (ProblemSetProblemModel problemSetProblemModel : problemSetProblemModels) {
            if (ProblemSetProblemType.BUNDLE.name().equals(problemSetProblemModel.type)) {
                map.put(problemSetProblemModel.problemJid, problemSetProblemModel.alias);
            }
        }

        return map;
    }

    @Override
    public ProblemSetProblem findProblemSetProblemByProblemSetJidAndProblemJid(String problemSetJid, String problemJid) {
        return ProblemSetProblemServiceUtils.createFromModel(problemSetProblemDao.findByProblemSetJidAndProblemJid(problemSetJid, problemJid));
    }
}
