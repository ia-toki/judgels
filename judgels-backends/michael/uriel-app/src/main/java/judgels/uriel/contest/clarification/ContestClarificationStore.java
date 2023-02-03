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
import judgels.uriel.api.contest.dump.ContestClarificationDump;
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

    public Optional<ContestClarification> answerClarification(
            String contestJid,
            String clarificationJid,
            String answer) {

        Optional<ContestClarificationModel> maybeClarification =
                clarificationDao.selectByContestJidAndClarificationJid(contestJid, clarificationJid);

        maybeClarification.ifPresent(clarification -> {
            if (clarification.status.equals(ContestClarificationStatus.ANSWERED.name())) {
                throw ContestErrors.clarificationAlreadyAnswered(clarification.jid);
            }
        });

        return maybeClarification.map(model -> {
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

    public Page<ContestClarification> getClarifications(
            String contestJid,
            Optional<String> status,
            Optional<Integer> page) {
        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(options::page);

        Page<ContestClarificationModel> contestClarificationModelPage;
        if (status.isPresent()) {
            contestClarificationModelPage = clarificationDao.selectPagedByContestJidAndStatus(
                    contestJid,
                    status.get(),
                    options.build());
        } else {
            contestClarificationModelPage = clarificationDao.selectPagedByContestJid(contestJid, options.build());
        }

        return contestClarificationModelPage.mapPage(
                p -> Lists.transform(p, ContestClarificationStore::fromModel));
    }

    public void importDump(String contestJid, ContestClarificationDump dump) {
        if (dump.getJid().isPresent() && clarificationDao.existsByJid(dump.getJid().get())) {
            throw ContestErrors.jidAlreadyExists(dump.getJid().get());
        }

        Optional<String> answer = dump.getAnswer();
        String status = answer.isPresent() && !answer.get().isEmpty()
                ? ContestClarificationStatus.ANSWERED.name()
                : ContestClarificationStatus.ASKED.name();

        ContestClarificationModel model = new ContestClarificationModel();
        model.contestJid = contestJid;
        model.topicJid = dump.getTopicJid();
        model.title = dump.getTitle();
        model.question = dump.getQuestion();
        model.answer = answer.orElse(null);
        model.status = status;
        clarificationDao.setModelMetadataFromDump(model, dump);
        clarificationDao.persist(model);
    }

    public Set<ContestClarificationDump> exportDumps(String contestJid, DumpImportMode mode) {
        return clarificationDao.selectAllByContestJid(contestJid, SelectionOptions.DEFAULT_ALL).stream().map(model -> {
            ContestClarificationDump.Builder builder = new ContestClarificationDump.Builder()
                    .mode(mode)
                    .topicJid(model.topicJid)
                    .title(model.title)
                    .question(model.question)
                    .answer(Optional.ofNullable(model.answer))
                    .title(model.title);

            if (mode == DumpImportMode.RESTORE) {
                builder
                        .jid(model.jid)
                        .createdAt(model.createdAt)
                        .createdBy(Optional.ofNullable(model.createdBy))
                        .createdIp(Optional.ofNullable(model.createdIp))
                        .updatedAt(model.updatedAt)
                        .updatedBy(Optional.ofNullable(model.updatedBy))
                        .updatedIp(Optional.ofNullable(model.updatedIp));
            }

            return builder.build();
        }).collect(Collectors.toSet());
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
