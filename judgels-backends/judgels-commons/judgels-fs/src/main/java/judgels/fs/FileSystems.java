package judgels.fs;

import java.nio.file.Path;
import java.util.Optional;
import judgels.contrib.fs.aws.AwsConfiguration;
import judgels.contrib.fs.aws.AwsFileSystem;
import judgels.contrib.fs.aws.AwsFsConfiguration;
import judgels.fs.local.LocalFileSystem;
import judgels.fs.local.LocalFsConfiguration;

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
