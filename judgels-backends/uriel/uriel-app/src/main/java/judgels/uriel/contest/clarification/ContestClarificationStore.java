package judgels.uriel.contest.clarification;

import com.google.common.collect.Lists;
import java.util.Optional;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
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

    public Page<ContestClarification> getClarifications(String contestJid, String userJid, SelectionOptions options) {
        return clarificationDao.selectPagedByContestJidAndUserJid(contestJid, userJid, options).mapData(
                data -> Lists.transform(data, ContestClarificationStore::fromModel));
    }

    public Page<ContestClarification> getClarifications(String contestJid, SelectionOptions options) {
        return clarificationDao.selectPagedByContestJid(contestJid, options).mapData(
                data -> Lists.transform(data, ContestClarificationStore::fromModel));
    }

    private static ContestClarification fromModel(ContestClarificationModel model) {
        return new ContestClarification.Builder()
                .id(model.id)
                .jid(model.jid)
                .userJid(model.createdBy)
                .topicJid(model.topicJid)
                .title(model.title)
                .question(model.question)
                .status(ContestClarificationStatus.valueOf(model.status))
                .time(model.createdAt)
                .answer(Optional.ofNullable(model.answer))
                .answererJid(Optional.ofNullable(model.answer == null ? null : model.updatedBy))
                .answeredTime(Optional.ofNullable(model.answer == null ? null : model.updatedAt))
                .build();
    }

    private static void toModel(String contestJid, ContestClarificationData data, ContestClarificationModel model) {
        model.contestJid = contestJid;
        model.topicJid = data.getTopicJid();
        model.title = data.getTitle();
        model.question = data.getQuestion();
        model.status = ContestClarificationStatus.ASKED.name();
    }
}
