package judgels.uriel.contest.clarification;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.clarification.ContestClarification;
import judgels.uriel.api.contest.clarification.ContestClarificationData;
import judgels.uriel.api.contest.clarification.ContestClarificationStatus;
import judgels.uriel.persistence.ContestClarificationDao;
import judgels.uriel.persistence.ContestClarificationModel;

public class ContestClarificationStore {
    private final ContestClarificationDao clarificationDao;

    @Inject
    public ContestClarificationStore(ContestClarificationDao clarificationDao) {
        this.clarificationDao = clarificationDao;
    }

    public ContestClarification createClarification(String contestJid, ContestClarificationData data) {
        ContestClarificationModel model = new ContestClarificationModel();
        toModel(contestJid, data, model);
        return fromModel(clarificationDao.insert(model));
    }

    public List<ContestClarification> getClarifications(String contestJid, String userJid) {
        return Lists.transform(
                clarificationDao.selectAllByContestJidAndUserJid(contestJid, userJid),
                ContestClarificationStore::fromModel);
    }

    private static ContestClarification fromModel(ContestClarificationModel model) {
        return new ContestClarification.Builder()
                .id(model.id)
                .jid(model.jid)
                .userJid(model.createdBy)
                .topic(model.topicJid)
                .title(model.title)
                .question(model.question)
                .status(ContestClarificationStatus.valueOf(model.status))
                .time(model.createdAt)
                .answererJid(Optional.ofNullable(model.updatedBy))
                .answer(Optional.ofNullable(model.answer))
                .answeredTime(Optional.ofNullable(model.updatedAt))
                .build();
    }

    private static void toModel(String contestJid, ContestClarificationData data, ContestClarificationModel model) {
        model.contestJid = contestJid;
        model.topicJid = data.getTopic();
        model.title = data.getTitle();
        model.question = data.getQuestion();
        model.status = ContestClarificationStatus.ASKED.name();
    }
}
