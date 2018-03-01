package judgels.uriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.uriel.contest.ContestResource;
import judgels.uriel.contest.contestant.ContestContestantResource;
import judgels.uriel.contest.scoreboard.ContestScoreboardResource;
import judgels.uriel.hibernate.UrielHibernateModule;
import judgels.uriel.jophiel.JophielModule;

@Component(modules = {
        JophielModule.class,
        UrielModule.class,
        UrielHibernateModule.class})
@Singleton
public interface UrielComponent {
    ContestResource contestResource();
    ContestScoreboardResource contestScoreboardResource();
    ContestContestantResource contestContestantResource();
}
