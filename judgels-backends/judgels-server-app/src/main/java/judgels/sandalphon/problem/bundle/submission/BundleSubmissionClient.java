package judgels.sandalphon.problem.bundle.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import judgels.fs.FileSystem;
import judgels.sandalphon.api.submission.bundle.BundleAnswer;
import judgels.sandalphon.api.submission.bundle.BundleGradingResult;
import judgels.sandalphon.api.submission.bundle.BundleSubmission;
import judgels.sandalphon.persistence.BundleGradingDao;
import judgels.sandalphon.persistence.BundleGradingModel;
import judgels.sandalphon.persistence.BundleSubmissionDao;
import judgels.sandalphon.persistence.BundleSubmissionModel;
import judgels.sandalphon.problem.base.submission.SubmissionFs;
import judgels.sandalphon.problem.bundle.grading.BundleProblemGrader;

public class BundleSubmissionClient {
    private final ObjectMapper mapper;
    private final FileSystem submissionFs;
    private final BundleSubmissionDao submissionDao;
    private final BundleGradingDao gradingDao;
    private final BundleProblemGrader grader;

    @Inject
    public BundleSubmissionClient(
            ObjectMapper mapper,
            @SubmissionFs FileSystem submissionFs,
            BundleSubmissionDao submissionDao,
            BundleGradingDao gradingDao,
            BundleProblemGrader grader) {

        this.mapper = mapper;
        this.submissionFs = submissionFs;
        this.submissionDao = submissionDao;
        this.gradingDao = gradingDao;
        this.grader = grader;
    }

    public BundleAnswer createBundleAnswerFromNewSubmission(MultivaluedMap<String, String> form, String language) {
        Map<String, String> answers = new HashMap<>();
        for (String jid : form.keySet()) {
            String answer = form.getFirst(jid);
            if (!answer.isEmpty()) {
                answers.put(jid, form.getFirst(jid));
            }
        }

        return new BundleAnswer.Builder()
                .answers(answers)
                .languageCode(language)
                .build();
    }

    public BundleAnswer createBundleAnswerFromPastSubmission(String submissionJid) {
        try {
            return mapper.readValue(submissionFs.readFromFile(Paths.get(submissionJid, "answer.json")), BundleAnswer.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void submit(String problemJid, BundleAnswer answer) {
        BundleSubmissionModel submissionModel = new BundleSubmissionModel();
        submissionModel.problemJid = problemJid;
        submissionDao.insert(submissionModel);

        grade(submissionModel, answer);

        submissionFs.createDirectory(Paths.get(submissionModel.jid));
        submissionFs.writeToFile(Paths.get(submissionModel.jid, "answer.json"), writeObj(answer));
    }

    public void regradeSubmission(BundleSubmission submission) {
        BundleSubmissionModel submissionModel = submissionDao.findByJid(submission.getJid());
        BundleAnswer answer = createBundleAnswerFromPastSubmission(submission.getJid());

        grade(submissionModel, answer);
    }

    public void regradeSubmissions(List<BundleSubmission> submissions) {
        for (BundleSubmission submission : submissions) {
            regradeSubmission(submission);
        }
    }

    private void grade(BundleSubmissionModel submissionModel, BundleAnswer answer) {
        BundleGradingResult result = grader.grade(submissionModel.problemJid, answer);

        BundleGradingModel gradingModel = new BundleGradingModel();
        gradingModel.submissionJid = submissionModel.jid;
        gradingModel.score = (int) result.getScore();
        gradingModel.details = writeObj(result.getDetails());
        gradingDao.insert(gradingModel);
    }

    private String writeObj(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
