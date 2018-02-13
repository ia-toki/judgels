package judgels.uriel.contest;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestStyle;

public class ContestStore {
    private final ContestDao contestDao;

    @Inject
    public ContestStore(ContestDao contestDao) {
        this.contestDao = contestDao;
    }

    public Optional<Contest> findContestByJid(String contestJid) {
        return contestDao.selectByJid(contestJid).map(ContestStore::fromModel);
    }

    public Page<Contest> getContests(int page, int pageSize) {
        Page<ContestModel> contestModelsPage = contestDao.selectAll(page, pageSize);

        List<Contest> contests = Lists.transform(contestModelsPage.getData(), ContestStore::fromModel);

        return new Page.Builder<Contest>()
                .totalItems(contestModelsPage.getTotalItems())
                .totalPages(contestModelsPage.getTotalPages())
                .data(contests)
                .build();
    }

    public Contest createContest(ContestData contestData) {
        ContestModel model = new ContestModel();
        toModel(contestData, model);
        return fromModel(contestDao.insert(model));
    }

    private static Contest fromModel(ContestModel model) {
        return new Contest.Builder()
                .jid(model.jid)
                .name(model.name)
                .description(model.description)
                .style(ContestStyle.valueOf(model.style))
                .build();
    }

    private static void toModel(ContestData data, ContestModel model) {
        model.name = data.getName();
        model.description = data.getDescription();
        model.style = data.getStyle().name();
    }
}
