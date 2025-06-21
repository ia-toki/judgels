package judgels.fs;

import java.nio.file.Path;
import java.util.Optional;
import judgels.fs.local.LocalFileSystem;
import judgels.fs.local.LocalFsConfiguration;
import tlx.fs.aws.AwsConfiguration;
import tlx.fs.aws.AwsFileSystem;
import tlx.fs.aws.AwsFsConfiguration;

public class FileSystems {
    private FileSystems() {}

    public static FileSystem get(FsConfiguration config, Optional<AwsConfiguration> awsConfig, Path localDataDir) {
        if (config instanceof AwsFsConfiguration) {
            return new AwsFileSystem(awsConfig.get(), (AwsFsConfiguration) config);
        }
        if (config instanceof LocalFsConfiguration) {
            return new LocalFileSystem(localDataDir);
        }
        return null;
    }
}
