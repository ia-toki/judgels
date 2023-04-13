package judgels.michael.problem.programming.grading.config;

import java.time.Instant;
import judgels.fs.FileInfo;

public abstract class BaseGradingConfigAdapterTests {
    protected FileInfo createFile(String name) {
        return new FileInfo.Builder()
                .name(name)
                .size(10)
                .lastModifiedTime(Instant.now())
                .build();
    }
}
