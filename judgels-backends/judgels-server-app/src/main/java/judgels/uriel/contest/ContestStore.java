package judgels.uriel.contest;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.stream.Collectors.toMap;

import com.google.common.collect.Lists;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestDescription;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.ContestInfo;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestDao.ContestQueryBuilder;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModel_;

@Singleton
public class ContestStore {
    private final ContestDao contestDao;

    @Inject
    public ContestStore(ContestDao contestDao) {
        this.contestDao = contestDao;
    }

    public Optional<Contest> getContestByJid(String contestJid) {
        return contestDao.selectByJid(contestJid).map(ContestStore::fromModel);
    }

    public Map<String, ContestInfo> getContestInfosByJids(Collection<String> contestJids) {
        return contestDao.selectByJids(contestJids)
                .values()
                .stream()
                .collect(toMap(
                        c -> c.jid,
                        c -> new ContestInfo.Builder()
                                .slug(c.slug)
                                .name(c.name)
                                .beginTime(c.beginTime)
                                .build()));
    }

    public Optional<Contest> getContestBySlug(String contestSlug) {
        return contestDao.selectBySlug(contestSlug).map(ContestStore::fromModel);
    }

    public Map<String, String> translateSlugsToJids(Collection<String> slugs) {
        return contestDao.selectAllBySlugs(slugs).stream()
                .collect(toMap(m -> m.slug, m -> m.jid));
    }

    public Page<Contest> getContests(Optional<String> userJid, Optional<String> nameFilter, int pageNumber, int pageSize) {
        ContestQueryBuilder query = contestDao.select();

        if (userJid.isPresent()) {
            query.whereUserCanView(userJid.get());
        }
        if (nameFilter.isPresent()) {
            query.whereNameLike(nameFilter.get());
        }

        return query
                .orderBy(ContestModel_.BEGIN_TIME, OrderDir.DESC)
                .paged(pageNumber, pageSize)
                .mapPage(p -> Lists.transform(p, ContestStore::fromModel));
    }

    public List<Contest> getActiveContests(Optional<String> userJid) {
        ContestQueryBuilder query = contestDao
                .select()
                .whereActive();

        if (userJid.isPresent()) {
            query.whereUserCanView(userJid.get());
        }

        return Lists.transform(query
                .orderBy(ContestModel_.BEGIN_TIME, OrderDir.ASC)
                .all(), ContestStore::fromModel);
    }

    public List<Contest> getRunningContests() {
        return Lists.transform(contestDao
                .select()
                .whereRunning()
                .orderBy(ContestModel_.BEGIN_TIME, OrderDir.ASC)
                .all(), ContestStore::fromModel);
    }

    public List<Contest> getPubliclyParticipatedContests(String userJid) {
        return Lists.transform(contestDao
                .select()
                .wherePublic()
                .whereEnded()
                .whereUserParticipated(userJid)
                .orderBy(ContestModel_.BEGIN_TIME, OrderDir.ASC)
                .all(), ContestStore::fromModel);
    }

    public List<Contest> getPublicContestsAfter(Instant time) {
        return Lists.transform(contestDao
                .select()
                .wherePublic()
                .whereEnded()
                .whereBeginsAfter(time)
                .orderBy(ContestModel_.BEGIN_TIME, OrderDir.ASC)
                .all(), ContestStore::fromModel);
    }

    public Contest createContest(ContestCreateData contestCreateData) {
        if (contestDao.selectBySlug(contestCreateData.getSlug()).isPresent()) {
            throw ContestErrors.slugAlreadyExists(contestCreateData.getSlug());
        }

        ContestModel model = new ContestModel();
        model.slug = contestCreateData.getSlug();
        model.name = contestCreateData.getSlug();
        model.description = "";
        model.style = ContestStyle.ICPC.name();
        model.beginTime = Instant.ofEpochSecond(4102444800L); // 1 January 2100
        model.duration = Duration.ofHours(5).toMillis();

        return fromModel(contestDao.insert(model));
    }

    public Contest updateContest(String contestJid, ContestUpdateData data) {
        ContestModel model = contestDao.findByJid(contestJid);
        if (data.getSlug().isPresent()) {
            String newSlug = data.getSlug().get();
            if (model.slug == null || !model.slug.equals(newSlug)) {
                if (contestDao.selectBySlug(newSlug).isPresent()) {
                    throw ContestErrors.slugAlreadyExists(newSlug);
                }
            }
        }

        data.getSlug().ifPresent(slug -> model.slug = slug);
        data.getName().ifPresent(name -> model.name = name);
        data.getStyle().ifPresent(style -> model.style = style.name());
        data.getBeginTime().ifPresent(time -> model.beginTime = time);
        data.getDuration().ifPresent(duration -> model.duration = duration.toMillis());
        return fromModel(contestDao.update(model));
    }

    public String getContestDescription(String contestJid) {
        return contestDao.findByJid(contestJid).description;
    }

    public ContestDescription updateContestDescription(String contestJid, ContestDescription description) {
        ContestModel model = contestDao.findByJid(contestJid);
        model.description = description.getDescription();
        contestDao.update(model);
        return description;
    }

    private static Contest fromModel(ContestModel model) {
        return new Contest.Builder()
                .id(model.id)
                .jid(model.jid)
                .slug(Optional.ofNullable(model.slug).orElse("" + model.id))
                .name(model.name)
                .style(ContestStyle.valueOf(model.style))
                .beginTime(model.beginTime)
                .duration(Duration.of(model.duration, MILLIS))
                .build();
    }
}
