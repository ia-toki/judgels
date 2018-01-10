package judgels.fs.local;

import java.io.InputStream;
import java.util.List;
import judgels.fs.FileSystem;

public class LocalFileSystem implements FileSystem {
    @Override
    public void uploadPublicFile(InputStream file, List<String> destDirPath, String destFilename) {
        throw new IllegalStateException();
    }

    @Override
    public String getPublicFileUrl(List<String> filePath) {
        throw new IllegalStateException();
    }
}
