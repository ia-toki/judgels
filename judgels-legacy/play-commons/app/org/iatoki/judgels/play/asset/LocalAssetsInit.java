package org.iatoki.judgels.play.asset;

import org.apache.commons.io.FileUtils;
import play.Environment;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;

@Singleton
public final class LocalAssetsInit {

    @Inject
    public LocalAssetsInit(Environment environment) {
        checkAndCopyFileToLocal(environment, "logo.png");
        checkAndCopyFileToLocal(environment, "logo-colored.png");
        checkAndCopyFileToLocal(environment, "favicon.ico");
    }

    private void checkAndCopyFileToLocal(Environment environment, String fileName) {
        File assetFile = new File(environment.getFile("external-assets"), fileName);
        if (!assetFile.exists()) {
            assetFile.getParentFile().mkdirs();
            try {
                FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/public/lib/playcommons/images/" + fileName), assetFile);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot create file " + fileName + ".");
            }
        }
    }
}
