package tlx.curriculum;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import tlx.TlxScope;

@Module
public class CurriculumModule {
    @Provides
    @TlxScope
    CurriculumCreator curriculumCreator(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            CurriculumStore curriculumStore) {
        return unitOfWorkAwareProxyFactory.create(
                CurriculumCreator.class,
                new Class<?>[]{CurriculumStore.class},
                new Object[]{curriculumStore});
    }
}
