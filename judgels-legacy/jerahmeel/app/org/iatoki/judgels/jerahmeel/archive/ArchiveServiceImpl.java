package org.iatoki.judgels.jerahmeel.archive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.iatoki.judgels.jerahmeel.scorecache.ProblemSetScoreCacheUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Stack;

@Singleton
public final class ArchiveServiceImpl implements ArchiveService {

    private final ArchiveDao archiveDao;

    @Inject
    public ArchiveServiceImpl(ArchiveDao archiveDao) {
        this.archiveDao = archiveDao;
    }

    @Override
    public boolean archiveExistsByJid(String archiveJid) {
        return archiveDao.existsByJid(archiveJid);
    }

    @Override
    public List<Archive> getAllArchives() {
        Stack<Archive> archiveStack = new Stack<>();
        archiveStack.add(null);

        ImmutableList.Builder<Archive> archiveBuilder = ImmutableList.builder();

        while (!archiveStack.isEmpty()) {
            Archive currentArchive = archiveStack.pop();

            String currentJid;
            if (currentArchive != null) {
                currentJid = currentArchive.getJid();
                archiveBuilder.add(currentArchive);
            } else {
                currentJid = "";
            }

            List<ArchiveModel> archiveModels = archiveDao.findSortedByFiltersEq("id", "asc", "", ImmutableMap.of(ArchiveModel_.parentJid, currentJid), 0, -1);
            for (ArchiveModel archiveModel : archiveModels) {
                Archive archive = ArchiveServiceUtils.createArchiveFromModel(archiveModel, currentArchive);
                if (currentArchive != null) {
                    currentArchive.getSubArchives().add(archive);
                }
                archiveStack.push(archive);
            }
        }

        return archiveBuilder.build();
    }

    @Override
    public List<Archive> getChildArchives(String parentJid) {
        return ArchiveServiceUtils.getChildArchives(archiveDao, parentJid);
    }

    @Override
    public List<ArchiveWithScore> getChildArchivesWithScore(String parentJid, String userJid) {
        List<Archive> directSubArchives = getChildArchives(parentJid);

        ImmutableList.Builder<ArchiveWithScore> directSubArchivesWithScore = ImmutableList.builder();
        for (Archive subArchive : directSubArchives) {
            double totalScore = ProblemSetScoreCacheUtils.getInstance().getArchiveScore(userJid, subArchive.getJid());

            directSubArchivesWithScore.add(new ArchiveWithScore(subArchive, totalScore));
        }

        return directSubArchivesWithScore.build();
    }

    @Override
    public Archive findArchiveById(long archiveId) throws ArchiveNotFoundException {
        ArchiveModel intendedArchiveModel = archiveDao.findById(archiveId);

        if (intendedArchiveModel == null) {
            throw new ArchiveNotFoundException("Archive Not Found.");
        }

        return ArchiveServiceUtils.createArchiveWithParentArchivesFromModel(archiveDao, intendedArchiveModel);
    }

    @Override
    public Archive findArchiveByJid(String archiveJid) {
        return ArchiveServiceUtils.createArchiveFromModel(archiveDao.findByJid(archiveJid), null);
    }

    @Override
    public long createArchive(String parentJid, String name, String description, String userJid, String userIpAddress) {
        ArchiveModel archiveModel = new ArchiveModel();
        archiveModel.parentJid = parentJid;
        archiveModel.name = name;
        archiveModel.description = description;

        archiveDao.persist(archiveModel, userJid, userIpAddress);

        return archiveModel.id;
    }

    @Override
    public void updateArchive(String archiveJid, String parentJid, String name, String description, String userJid, String userIpAddress) {
        ArchiveModel archiveModel = archiveDao.findByJid(archiveJid);
        archiveModel.parentJid = parentJid;
        archiveModel.name = name;
        archiveModel.description = description;

        archiveDao.edit(archiveModel, userJid, userIpAddress);
    }
}
