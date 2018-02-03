package judgels.uriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.uriel.contest.ContestResource;
import judgels.uriel.hibernate.UrielHibernateModule;

@Component(modules = {
        UrielModule.class,
        UrielHibernateModule.class})
@Singleton
public interface UrielComponent {
    ContestResource contestResource();
}
