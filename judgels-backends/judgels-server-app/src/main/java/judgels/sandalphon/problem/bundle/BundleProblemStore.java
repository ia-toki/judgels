package judgels.sandalphon.problem.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.sandalphon.api.problem.bundle.BundleItemsConfig;
import judgels.sandalphon.problem.base.ProblemFs;

public final class BundleProblemStore extends AbstractBundleProblemStore {
    private final ObjectMapper mapper;
    private final FileSystem problemFs;

    @Inject
    public BundleProblemStore(ObjectMapper mapper, @ProblemFs FileSystem problemFs) {
        super(mapper, problemFs);
        this.mapper = mapper;
        this.problemFs = problemFs;
    }

    public void initBundleProblem(String problemJid) {
        problemFs.createDirectory(getItemsDirPath(problemJid, null));

        BundleItemsConfig config = new BundleItemsConfig.Builder().build();
        problemFs.writeToFile(getItemsConfigFilePath(problemJid, null), writeItemsConfig(config));
    }

    private String writeItemsConfig(BundleItemsConfig config) {
        try {
            return mapper.writeValueAsString(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
