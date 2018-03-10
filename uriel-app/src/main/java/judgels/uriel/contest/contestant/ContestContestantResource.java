package judgels.uriel.contest.contestant;

import static judgels.service.ServiceUtils.checkFound;
import static judgels.uriel.contest.ContestHacks.checkAllowed;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.user.UserService;
import judgels.persistence.api.Page;
import judgels.uriel.api.contest.ContestContestant;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.contest.ContestStore;

public class ContestContestantResource implements ContestContestantService {
    private final ContestStore contestStore;
    private final ContestContestantStore contestantStore;
    private final UserService userService;

    @Inject
    public ContestContestantResource(ContestStore contestStore, ContestContestantStore contestantStore,
            UserService userService) {
        this.contestStore = contestStore;
        this.contestantStore = contestantStore;
        this.userService = userService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<ContestContestant> getContestants(String contestJid, int page, int pageSize) {
        checkAllowed(checkFound(contestStore.findContestByJid(contestJid)));

        Page<String> contestantJids = contestantStore.getContestantJids(contestJid, page, pageSize);
        return contestantJids.mapData(jids ->
                userService.findPublicUsersByJids(ImmutableSet.copyOf(jids)).values().stream()
                        .map(publicUser -> new ContestContestant.Builder().contestant(publicUser).build())
                        .collect(Collectors.toList()));
    }

    @Override
    @UnitOfWork
    public Set<String> addContestants(String contestJid, Set<String> contestantJids) {
        checkAllowed(checkFound(contestStore.findContestByJid(contestJid)));

        return contestantStore.addContestants(contestJid, contestantJids);
    }

}
