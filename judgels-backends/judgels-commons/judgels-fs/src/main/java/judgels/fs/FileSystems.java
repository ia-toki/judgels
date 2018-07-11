package judgels.fs;

import java.nio.file.Path;
import java.util.Optional;
import judgels.fs.aws.AwsConfiguration;
import judgels.fs.aws.AwsFileSystem;
import judgels.fs.aws.AwsFsConfiguration;
import judgels.fs.duplex.DuplexFileSystem;
import judgels.fs.duplex.DuplexFsConfiguration;
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
        return new DuplexFileSystem(
                new LocalFileSystem(localDataDir),
                new AwsFileSystem(awsConfig.get(), ((DuplexFsConfiguration) config).toAwsFsConfig()));
    }
}
