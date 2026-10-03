package judgels.training.curriculum;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;

@Module
public class CurriculumModule {
    @Provides
    @Singleton
    CurriculumCreator curriculumCreator(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            CurriculumStore curriculumStore) {
        return unitOfWorkAwareProxyFactory.create(
                CurriculumCreator.class,
                new Class<?>[]{CurriculumStore.class},
                new Object[]{curriculumStore});
    }
}
