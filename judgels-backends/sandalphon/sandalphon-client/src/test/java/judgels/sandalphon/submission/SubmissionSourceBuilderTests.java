package judgels.sandalphon.submission;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import java.nio.file.Paths;
import judgels.fs.InMemoryFileSystem;
import judgels.gabriel.api.SourceFile;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubmissionSourceBuilderTests {
    private static final String SUBMISSION = "submissionJid";

    private InMemoryFileSystem submissionFs;

    private SubmissionSourceBuilder sourceBuilder;

    @BeforeEach
    void before() {
        submissionFs = new InMemoryFileSystem();
        sourceBuilder = new SubmissionSourceBuilder(submissionFs) {};
    }

    @Test
    void past_submission() {
        submissionFs.addFile(Paths.get(SUBMISSION, "encoder"), "my-encoder.cpp", "the encoder".getBytes());
        submissionFs.addFile(Paths.get(SUBMISSION, "decoder"), "my-decoder.cpp", "the decoder".getBytes());

        SourceFile encoder = new SourceFile.Builder()
                .name("my-encoder.cpp")
                .content("the encoder".getBytes())
                .build();
        SourceFile decoder = new SourceFile.Builder()
                .name("my-decoder.cpp")
                .content("the decoder".getBytes())
                .build();

        assertThat(sourceBuilder.fromPastSubmission(SUBMISSION).getSubmissionFiles()).isEqualTo(ImmutableMap.of(
                "encoder", encoder,
                "decoder", decoder));
    }
}
