package judgels.fs;

import java.io.InputStream;
import java.util.List;

public interface FileSystem {
    void uploadPublicFile(InputStream file, List<String> destDirPath, String destFilename);
    String getPublicFileUrl(List<String> filePath);
}
