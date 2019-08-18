package judgels.uriel.contest;

import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.uriel.api.contest.group.ContestGroup;
import judgels.uriel.api.contest.group.ContestGroupCreateData;
import judgels.uriel.api.contest.group.ContestGroupErrors;
import judgels.uriel.persistence.ContestGroupContestDao;
import judgels.uriel.persistence.ContestGroupDao;
import judgels.uriel.persistence.ContestGroupModel;

@Singleton
public class ContestGroupStore {
    private final ContestGroupDao contestGroupDao;
    private final ContestGroupContestDao contestGroupContestDao;

    @Inject
    public ContestGroupStore(ContestGroupDao contestGroupDao, ContestGroupContestDao contestGroupContestDao) {
        this.contestGroupDao = contestGroupDao;
        this.contestGroupContestDao = contestGroupContestDao;
    }

    public ContestGroup createContestGroup(ContestGroupCreateData data) {
        if (contestGroupDao.selectBySlug(data.getSlug()).isPresent()) {
            throw ContestGroupErrors.slugAlreadyExists(data.getSlug());
        }

        ContestGroupModel model = new ContestGroupModel();
        model.slug = data.getSlug();
        model.name = data.getSlug();

        return fromModel(contestGroupDao.insert(model));
    }

    public Set<ContestGroup> getContestGroupsByContestJids(Set<String> contestJids) {
        Set<String> contestGroupJids = contestGroupContestDao.selectAllContestGroupJidsByContestJids(contestJids);
        return contestGroupDao.selectByJids(contestGroupJids).values()
                .stream()
                .map(ContestGroupStore::fromModel)
                .collect(Collectors.toSet());
    }

    private static ContestGroup fromModel(ContestGroupModel model) {
        return new ContestGroup.Builder()
                .jid(model.jid)
                .slug(model.slug)
                .name(model.name)
                .build();
    }
}
