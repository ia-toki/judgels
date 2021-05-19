package judgels.play.internal;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.tasks.WorkResult;
import org.gradle.api.tasks.WorkResults;
import judgels.play.WebJarsExtractSpec;
import org.webjars.WebJarExtractor;

public class WebJarsExtractor implements Serializable {
    public WorkResult execute(WebJarsExtractSpec spec) {
        List<URL> urls = new ArrayList<>();

        spec.getClasspath().forEach(file -> {
            try {
                urls.add(file.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
        URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
        WebJarExtractor extractor = new WebJarExtractor(classLoader);

        File outputDir = new File(spec.getDestinationDir(), "lib");
        try {
            extractor.extractAllWebJarsTo(outputDir);
            extractor.extractAllNodeModulesTo(outputDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return WorkResults.didWork(true);
    }
}
