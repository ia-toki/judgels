package judgels.catalog;

import com.google.common.collect.Lists;
import jakarta.ws.rs.BadRequestException;
import java.util.List;
import judgels.api.catalog.CatalogFile;
import judgels.core.fs.FileInfo;

public class CatalogFiles {
    private CatalogFiles() {}

    public static List<CatalogFile> fromFileInfos(List<FileInfo> files) {
        return Lists.transform(files, f -> new CatalogFile.Builder()
                .name(f.getName())
                .size(f.getSize())
                .lastModifiedTime(f.getLastModifiedTime())
                .build());
    }

    // A file sits directly in its directory, so its name must not lead anywhere else.
    public static void checkFilename(String filename) {
        if (filename == null
                || filename.isEmpty()
                || filename.startsWith(".")
                || filename.contains("/")
                || filename.contains("\\")) {
            throw new BadRequestException();
        }
    }
}
