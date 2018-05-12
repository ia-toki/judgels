package judgels.sandalphon.submission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import java.nio.file.Paths;
import judgels.fs.InMemoryFileSystem;
import judgels.gabriel.api.SourceFile;
import judgels.sandalphon.api.submission.Submission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubmissionSourceFetcherTests {
    private static final String SUBMISSION_JID = "submissionJid";

    private InMemoryFileSystem submissionFs;

    private SubmissionSourceFetcher submissionFetcher;
    private Submission submission;

    @BeforeEach
    void before() {
        submissionFs = new InMemoryFileSystem();
        submissionFetcher = new SubmissionSourceFetcher(submissionFs);

        submission = mock(Submission.class);
        when(submission.getJid()).thenReturn(SUBMISSION_JID);
    }

    @Test
    void fetch_submission() {
        submissionFs.addFile(Paths.get(SUBMISSION_JID, "encoder"), "my-encoder.cpp", "the encoder".getBytes());
        submissionFs.addFile(Paths.get(SUBMISSION_JID, "decoder"), "my-decoder.cpp", "the decoder".getBytes());

        SourceFile encoder = new SourceFile.Builder()
                .name("my-encoder.cpp")
                .content("the encoder".getBytes())
                .build();
        SourceFile decoder = new SourceFile.Builder()
                .name("my-decoder.cpp")
                .content("the decoder".getBytes())
                .build();

        assertThat(submissionFetcher.fetchSubmissionSource(submission).getFiles()).isEqualTo(ImmutableMap.of(
                "encoder", encoder,
                "decoder", decoder));
    }
}
