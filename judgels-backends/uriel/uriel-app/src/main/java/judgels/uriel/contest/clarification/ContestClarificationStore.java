package judgels.uriel.contest.clarification;

import com.google.common.collect.Lists;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.api.dump.DumpImportMode;
import judgels.uriel.api.contest.ContestErrors;
import judgels.uriel.api.contest.clarification.ContestClarification;
import judgels.uriel.api.contest.clarification.ContestClarificationData;
import judgels.uriel.api.contest.clarification.ContestClarificationStatus;
import judgels.uriel.api.dump.ContestClarificationDump;
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

    public Optional<ContestClarification> updateClarificationAnswer(
            String contestJid,
            String clarificationJid,
            String answer) {

        return clarificationDao.selectByContestJidAndClarificationJid(contestJid, clarificationJid).map(model -> {
            model.answer = answer;
            model.status = ContestClarificationStatus.ANSWERED.name();
            return fromModel(clarificationDao.update(model));
        });
    }

    public Page<ContestClarification> getClarifications(String contestJid, String userJid, Optional<Integer> page) {
        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(options::page);

        return clarificationDao.selectPagedByContestJidAndUserJid(contestJid, userJid, options.build()).mapPage(
                p -> Lists.transform(p, ContestClarificationStore::fromModel));
    }

    public Page<ContestClarification> getClarifications(String contestJid, Optional<Integer> page) {
        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(options::page);

        return clarificationDao.selectPagedByContestJid(contestJid, options.build()).mapPage(
                p -> Lists.transform(p, ContestClarificationStore::fromModel));
    }

    public void importDump(String contestJid, ContestClarificationDump contestClarificationDump) {
        if (contestClarificationDump.getJid().isPresent()
                && clarificationDao.selectByJid(contestClarificationDump.getJid().get()).isPresent()) {
            throw ContestErrors.jidAlreadyExists(contestClarificationDump.getJid().get());
        }

        Optional<String> answer = contestClarificationDump.getAnswer();
        String status = answer.isPresent() && !answer.get().isEmpty()
                ? ContestClarificationStatus.ANSWERED.name()
                : ContestClarificationStatus.ASKED.name();

        ContestClarificationModel contestClarificationModel = new ContestClarificationModel();
        contestClarificationModel.contestJid = contestJid;
        contestClarificationModel.topicJid = contestClarificationDump.getTopicJid();
        contestClarificationModel.title = contestClarificationDump.getTitle();
        contestClarificationModel.question = contestClarificationDump.getQuestion();
        contestClarificationModel.answer = answer.orElse(null);
        contestClarificationModel.status = status;
        clarificationDao.setModelMetadataFromDump(contestClarificationModel, contestClarificationDump);
        clarificationDao.persist(contestClarificationModel);
    }

    public Set<ContestClarificationDump> exportDumps(String contestJid) {
        return clarificationDao.selectAllByContestJid(contestJid, SelectionOptions.DEFAULT_ALL).stream()
                .map(contestClarificationModel -> new ContestClarificationDump.Builder()
                        .mode(DumpImportMode.RESTORE)
                        .topicJid(contestClarificationModel.topicJid)
                        .title(contestClarificationModel.title)
                        .question(contestClarificationModel.question)
                        .answer(Optional.ofNullable(contestClarificationModel.answer))
                        .title(contestClarificationModel.title)
                        .jid(contestClarificationModel.jid)
                        .createdAt(contestClarificationModel.createdAt)
                        .createdBy(Optional.ofNullable(contestClarificationModel.createdBy))
                        .createdIp(Optional.ofNullable(contestClarificationModel.createdIp))
                        .updatedAt(contestClarificationModel.updatedAt)
                        .updatedBy(Optional.ofNullable(contestClarificationModel.updatedBy))
                        .updatedIp(Optional.ofNullable(contestClarificationModel.updatedIp))
                        .build())
                .collect(Collectors.toSet());
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
