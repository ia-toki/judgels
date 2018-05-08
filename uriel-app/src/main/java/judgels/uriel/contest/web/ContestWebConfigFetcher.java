package judgels.uriel.contest.web;

import static judgels.uriel.api.contest.web.ContestTab.ANNOUNCEMENTS;
import static judgels.uriel.api.contest.web.ContestTab.SCOREBOARD;
import static judgels.uriel.api.contest.web.ContestTab.SUBMISSIONS;

import com.google.common.collect.ImmutableSet;
import javax.inject.Inject;
import judgels.uriel.api.contest.web.ContestTab;
import judgels.uriel.api.contest.web.ContestWebConfig;
import judgels.uriel.role.RoleChecker;

public class ContestWebConfigFetcher {
    private final RoleChecker roleChecker;

    @Inject
    public ContestWebConfigFetcher(RoleChecker roleChecker) {
        this.roleChecker = roleChecker;
    }

    public ContestWebConfig getConfig(String userJid, String contestJid) {
        ImmutableSet.Builder<ContestTab> visibleTabs = ImmutableSet.builder();
        visibleTabs.add(ANNOUNCEMENTS, SCOREBOARD);

        if (roleChecker.canViewOwnSubmissions(userJid, contestJid)) {
            visibleTabs.add(SUBMISSIONS);
        }

        return new ContestWebConfig.Builder()
                .visibleTabs(visibleTabs.build())
                .build();
    }
}
