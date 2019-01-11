package org.iatoki.judgels.jerahmeel.chapter.problem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.iatoki.judgels.jerahmeel.chapter.ChapterScoreCacheUtils;
import org.iatoki.judgels.jerahmeel.user.item.UserItemDao;
import org.iatoki.judgels.jerahmeel.user.item.UserItemModel;
import org.iatoki.judgels.jerahmeel.user.item.UserItemStatus;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public final class ChapterProblemServiceImpl implements ChapterProblemService {

    private final ChapterProblemDao chapterProblemDao;
    private final UserItemDao userItemDao;

    @Inject
    public ChapterProblemServiceImpl(ChapterProblemDao chapterProblemDao, UserItemDao userItemDao) {
        this.chapterProblemDao = chapterProblemDao;
        this.userItemDao = userItemDao;
    }

    @Override
    public boolean aliasExistsInChapter(String chapterJid, String alias) {
        return chapterProblemDao.existsByChapterJidAndAlias(chapterJid, alias);
    }

    @Override
    public ChapterProblem findChapterProblemById(long chapterProblemId) throws ChapterProblemNotFoundException {
        ChapterProblemModel chapterProblemModel = chapterProblemDao.findById(chapterProblemId);
        if (chapterProblemModel != null) {
            return ChapterProblemServiceUtils.createFromModel(chapterProblemModel);
        } else {
            throw new ChapterProblemNotFoundException("Chapter Problem Not Found");
        }
    }

    @Override
    public Page<ChapterProblem> getPageOfChapterProblems(String chapterJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = chapterProblemDao.countByFiltersEq(filterString, ImmutableMap.of(ChapterProblemModel_.chapterJid, chapterJid));
        List<ChapterProblemModel> chapterProblemModels = chapterProblemDao.findSortedByFiltersEq(orderBy, orderDir, filterString, ImmutableMap.of(ChapterProblemModel_.chapterJid, chapterJid), pageIndex * pageSize, pageSize);

        List<ChapterProblem> chapterProblems = chapterProblemModels.stream().map(m -> ChapterProblemServiceUtils.createFromModel(m)).collect(Collectors.toList());

        return new Page<>(chapterProblems, totalPages, pageIndex, pageSize);

    }

    @Override
    public Page<ChapterProblemWithProgress> getPageOfChapterProblemsWithProgress(String userJid, String chapterJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = chapterProblemDao.countByFilters(filterString, ImmutableMap.of(ChapterProblemModel_.chapterJid, chapterJid, ChapterProblemModel_.status, ChapterProblemStatus.VISIBLE.name()), ImmutableMap.of());
        List<ChapterProblemModel> chapterProblemModels = chapterProblemDao.findSortedByFilters(orderBy, orderDir, filterString, ImmutableMap.of(ChapterProblemModel_.chapterJid, chapterJid, ChapterProblemModel_.status, ChapterProblemStatus.VISIBLE.name()), ImmutableMap.of(), pageIndex * pageSize, pageSize);

        ImmutableList.Builder<ChapterProblemWithProgress> chapterProblemProgressBuilder = ImmutableList.builder();
        for (ChapterProblemModel chapterProblemModel : chapterProblemModels) {
            ProblemProgress progress = ProblemProgress.NOT_VIEWED;
            if (userItemDao.existsByUserJidAndItemJid(userJid, chapterProblemModel.problemJid)) {
                UserItemModel userItemModel = userItemDao.findByUserJidAndItemJid(userJid, chapterProblemModel.problemJid);
                if (UserItemStatus.VIEWED.name().equals(userItemModel.status)) {
                    progress = ProblemProgress.VIEWED;
                } else if (UserItemStatus.COMPLETED.name().equals(userItemModel.status)) {
                    progress = ProblemProgress.COMPLETED;
                }
            }

            double maxScore = ChapterScoreCacheUtils.getInstance().getUserMaxScoreFromChapterProblemModel(userJid, chapterProblemModel);

            chapterProblemProgressBuilder.add(new ChapterProblemWithProgress(ChapterProblemServiceUtils.createFromModel(chapterProblemModel), progress, maxScore));
        }

        return new Page<>(chapterProblemProgressBuilder.build(), totalPages, pageIndex, pageSize);
    }

    @Override
    public void addChapterProblem(String chapterJid, String problemJid, String problemSecret, String alias, ChapterProblemType type, ChapterProblemStatus status, String userJid, String userIpAddress) {
        ChapterProblemModel chapterProblemModel = new ChapterProblemModel();
        chapterProblemModel.chapterJid = chapterJid;
        chapterProblemModel.problemJid = problemJid;
        chapterProblemModel.problemSecret = problemSecret;
        chapterProblemModel.alias = alias;
        chapterProblemModel.type = type.name();
        chapterProblemModel.status = status.name();

        chapterProblemDao.persist(chapterProblemModel, userJid, userIpAddress);
    }

    @Override
    public void updateChapterProblem(long chapterProblemId, String alias, ChapterProblemStatus status, String userJid, String userIpAddress) {
        ChapterProblemModel chapterProblemModel = chapterProblemDao.findById(chapterProblemId);
        chapterProblemModel.alias = alias;
        chapterProblemModel.status = status.name();

        chapterProblemDao.edit(chapterProblemModel, userJid, userIpAddress);
    }

    @Override
    public void removeChapterProblem(long chapterProblemId) {
        ChapterProblemModel chapterProblemModel = chapterProblemDao.findById(chapterProblemId);

        chapterProblemDao.remove(chapterProblemModel);
    }

    @Override
    public Map<String, String> getProgrammingProblemJidToAliasMapByChapterJid(String chapterJid) {
        List<ChapterProblemModel> chapterProblemModels = chapterProblemDao.getByChapterJid(chapterJid);

        Map<String, String> map = Maps.newLinkedHashMap();

        for (ChapterProblemModel chapterProblemModel : chapterProblemModels) {
            if (ChapterProblemType.PROGRAMMING.name().equals(chapterProblemModel.type)) {
                map.put(chapterProblemModel.problemJid, chapterProblemModel.alias);
            }
        }

        return map;
    }

    @Override
    public Map<String, String> getBundleProblemJidToAliasMapByChapterJid(String chapterJid) {
        List<ChapterProblemModel> chapterProblemModels = chapterProblemDao.getByChapterJid(chapterJid);

        Map<String, String> map = Maps.newLinkedHashMap();

        for (ChapterProblemModel chapterProblemModel : chapterProblemModels) {
            if (ChapterProblemType.BUNDLE.name().equals(chapterProblemModel.type)) {
                map.put(chapterProblemModel.problemJid, chapterProblemModel.alias);
            }
        }

        return map;
    }

    @Override
    public ChapterProblem findChapterProblemByChapterJidAndProblemJid(String chapterJid, String problemJid) {
        return ChapterProblemServiceUtils.createFromModel(chapterProblemDao.findByChapterJidAndProblemJid(chapterJid, problemJid));
    }
}
