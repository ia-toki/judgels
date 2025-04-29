package judgels.sandalphon.problem.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import java.io.IOException;
import judgels.fs.FileSystem;
import judgels.sandalphon.api.problem.bundle.BundleItemsConfig;
import judgels.sandalphon.problem.base.ProblemFs;

public final class BundleProblemStore extends BaseBundleProblemStore {
    private final ObjectMapper mapper;
    private final FileSystem problemFs;

    @Inject
    public BundleProblemStore(ObjectMapper mapper, @ProblemFs FileSystem problemFs) {
        super(mapper, problemFs);
        this.mapper = mapper;
        this.problemFs = problemFs;
    }

    public void initBundleProblem(String problemJid) {
        problemFs.createDirectory(getItemsDirPath(null, problemJid));

        BundleItemsConfig config = new BundleItemsConfig.Builder().build();
        problemFs.writeToFile(getItemsConfigFilePath(null, problemJid), writeItemsConfig(config));
    }

    private String writeItemsConfig(BundleItemsConfig config) {
        try {
            return mapper.writeValueAsString(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
