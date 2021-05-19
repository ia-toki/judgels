package judgels.play.internal;

import org.gradle.util.GFileUtils;

import javax.inject.Inject;
import judgels.play.WebJarsExtractSpec;

public class WebJarsExtractRunnable implements Runnable {
    private final WebJarsExtractSpec spec;

    @Inject
    public WebJarsExtractRunnable(WebJarsExtractSpec spec) {
        this.spec = spec;
    }

    @Override
    public void run() {
        GFileUtils.forceDelete(spec.getDestinationDir());
        new WebJarsExtractor().execute(spec);
    }
}
