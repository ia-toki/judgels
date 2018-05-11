package judgels.fs;

import java.io.InputStream;
import java.nio.file.Path;

public interface FileSystem {
    void uploadPublicFile(InputStream file, Path destDirPath, String destFilename);
    String getPublicFileUrl(Path filePath);
}
