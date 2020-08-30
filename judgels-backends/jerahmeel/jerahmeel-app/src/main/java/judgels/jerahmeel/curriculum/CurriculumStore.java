package judgels.jerahmeel.curriculum;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.CurriculumDao;

public class CurriculumStore {
    private final CurriculumDao curriculumDao;

    private final LoadingCache<Long, String> curriculumDescriptionCache;

    @Inject
    public CurriculumStore(CurriculumDao curriculumDao) {
        this.curriculumDao = curriculumDao;

        this.curriculumDescriptionCache =  Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(Duration.ofMinutes(30))
                .build(this::getCurriculumDescriptionUncached);
    }

    public Optional<String> getCurriculumDescription() {
        return Optional.ofNullable(curriculumDescriptionCache.get(1L));
    }

    private String getCurriculumDescriptionUncached(long id) {
        return curriculumDao.select(id).map(m -> m.description).orElse(null);
    }
}
