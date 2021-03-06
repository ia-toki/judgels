package judgels.uriel.contest.module;

import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION_TIME_LIMIT;
import static judgels.uriel.api.contest.module.ContestModuleType.DIVISION;
import static judgels.uriel.api.contest.module.ContestModuleType.EDITORIAL;
import static judgels.uriel.api.contest.module.ContestModuleType.EXTERNAL_SCOREBOARD;
import static judgels.uriel.api.contest.module.ContestModuleType.FROZEN_SCOREBOARD;
import static judgels.uriel.api.contest.module.ContestModuleType.HIDDEN;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.gabriel.api.LanguageRestriction;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.module.ClarificationTimeLimitModuleConfig;
import judgels.uriel.api.contest.module.DivisionModuleConfig;
import judgels.uriel.api.contest.module.EditorialModuleConfig;
import judgels.uriel.api.contest.module.ExternalScoreboardModuleConfig;
import judgels.uriel.api.contest.module.FrozenScoreboardModuleConfig;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.persistence.ContestStyleModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestModuleModel.class, ContestStyleModel.class})
class ContestModuleStoreIntegrationTests extends AbstractIntegrationTests {
    private ContestStore contestStore;
    private ContestModuleStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        contestStore = component.contestStore();
        store = component.contestModuleStore();
    }

    @Test
    void crud_flow() {
        Contest contest = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        contestStore.createContest(new ContestCreateData.Builder().slug("contest-b").build());

        IoiStyleModuleConfig config = new IoiStyleModuleConfig.Builder()
                .gradingLanguageRestriction(LanguageRestriction.noRestriction())
                .usingLastAffectingPenalty(true)
                .build();
        store.upsertIoiStyleModule(contest.getJid(), config);
        assertThat(store.getIoiStyleModuleConfig(contest.getJid())).isEqualTo(config);

        FrozenScoreboardModuleConfig config1 = new FrozenScoreboardModuleConfig.Builder()
                .isOfficialScoreboardAllowed(false)
                .freezeDurationBeforeEndTime(Duration.ofHours(1))
                .build();
        store.upsertFrozenScoreboardModule(contest.getJid(), config1);
        assertThat(store.getFrozenScoreboardModuleConfig(contest.getJid())).contains(config1);

        FrozenScoreboardModuleConfig config2 = new FrozenScoreboardModuleConfig.Builder()
                .isOfficialScoreboardAllowed(false)
                .freezeDurationBeforeEndTime(Duration.ofHours(1))
                .build();
        store.upsertFrozenScoreboardModule(contest.getJid(), config2);

        store.upsertPausedModule(contest.getJid());
        store.disablePausedModule(contest.getJid());
        store.upsertPausedModule(contest.getJid());
        store.disablePausedModule(contest.getJid());

        ClarificationTimeLimitModuleConfig config3 = new ClarificationTimeLimitModuleConfig.Builder()
                .clarificationDuration(Duration.ofHours(1))
                .build();
        store.upsertClarificationTimeLimitModule(contest.getJid(), config3);
        store.upsertDivisionModule(contest.getJid(), new DivisionModuleConfig.Builder().division(2).build());
        store.upsertEditorialModule(contest.getJid(), new EditorialModuleConfig.Builder()
                .preface("<p>Thank you</p>")
                .build());
        ExternalScoreboardModuleConfig config4 = new ExternalScoreboardModuleConfig.Builder()
                .receiverUrl("http://scoreboard")
                .receiverSecret("secret")
                .build();
        store.upsertExternalScoreboardModule(contest.getJid(), config4);
        store.upsertHiddenModule(contest.getJid());

        assertThat(store.getEnabledModules(contest.getJid())).containsOnly(
                FROZEN_SCOREBOARD, CLARIFICATION_TIME_LIMIT, DIVISION, EDITORIAL, EXTERNAL_SCOREBOARD, HIDDEN);
    }
}
