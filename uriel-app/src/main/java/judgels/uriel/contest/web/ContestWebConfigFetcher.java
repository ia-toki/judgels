package judgels.uriel.contest.web;

import static judgels.uriel.api.contest.web.ContestTab.ANNOUNCEMENTS;
import static judgels.uriel.api.contest.web.ContestTab.PROBLEMS;
import static judgels.uriel.api.contest.web.ContestTab.SCOREBOARD;
import static judgels.uriel.api.contest.web.ContestTab.SUBMISSIONS;

import com.google.common.collect.ImmutableSet;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.web.ContestTab;
import judgels.uriel.api.contest.web.ContestWebConfig;
import judgels.uriel.role.RoleChecker;

public class ContestWebConfigFetcher {
    private final RoleChecker roleChecker;

    @Inject
    public ContestWebConfigFetcher(RoleChecker roleChecker) {
        this.roleChecker = roleChecker;
    }

    public ContestWebConfig fetchConfig(String userJid, Contest contest) {
        ImmutableSet.Builder<ContestTab> visibleTabs = ImmutableSet.builder();
        visibleTabs.add(ANNOUNCEMENTS);

        if (roleChecker.canViewProblems(userJid, contest)) {
            visibleTabs.add(PROBLEMS);
        }

        if (roleChecker.canViewDefaultScoreboard(userJid, contest)) {
            visibleTabs.add(SCOREBOARD);
        }

        if (roleChecker.canViewOwnSubmissions(userJid, contest)) {
            visibleTabs.add(SUBMISSIONS);
        }


        return new ContestWebConfig.Builder()
                .visibleTabs(visibleTabs.build())
                .build();
    }
}
