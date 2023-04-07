package judgels.sandalphon.problem.bundle.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.sandalphon.api.submission.bundle.BundleAnswer;
import judgels.sandalphon.problem.base.submission.SubmissionFs;

public class BundleSubmissionClient {
    private final ObjectMapper mapper;
    private final FileSystem submissionFs;

    @Inject
    public BundleSubmissionClient(
            ObjectMapper mapper,
            @SubmissionFs FileSystem submissionFs) {

        this.mapper = mapper;
        this.submissionFs = submissionFs;
    }


    public BundleAnswer createBundleAnswerFromPastSubmission(String submissionJid) {
        try {
            return mapper.readValue(submissionFs.readFromFile(Paths.get(submissionJid, "answer.json")), BundleAnswer.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
