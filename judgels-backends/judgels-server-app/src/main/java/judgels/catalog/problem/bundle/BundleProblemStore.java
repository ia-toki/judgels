package judgels.catalog.problem.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import java.io.IOException;
import judgels.api.catalog.problem.bundle.BundleItemsConfig;
import judgels.catalog.problem.ProblemFs;
import judgels.core.fs.FileSystem;

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
