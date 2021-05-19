package judgels.play.tasks;

import judgels.play.WebJarsExtractSpec;
import judgels.play.internal.DefaultWebJarsExtractSpec;
import judgels.play.internal.WebJarsExtractRunnable;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.WorkerExecutor;

public class WebJarsExtract extends DefaultTask {
    private final WorkerExecutor workerExecutor;
    private final Property<Directory> outputDirectory;
    private final ConfigurableFileCollection webJarsClasspath;
    private final ConfigurableFileCollection webJarsExtractorClasspath;

    @Inject
    public WebJarsExtract(WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor;
        this.outputDirectory = getProject().getObjects().directoryProperty();
        this.webJarsClasspath = getProject().files();
        this.webJarsExtractorClasspath = getProject().files();
    }

    @OutputDirectory
    public Property<Directory> getOutputDirectory() {
        return outputDirectory;
    }

    @InputFiles
    public ConfigurableFileCollection getWebJarsClasspath() {
        return webJarsClasspath;
    }

    @Classpath
    public ConfigurableFileCollection getWebJarsExtractorClasspath() {
        return webJarsExtractorClasspath;
    }

    @TaskAction
    public void extract() {
        final WebJarsExtractSpec
                spec = new DefaultWebJarsExtractSpec(getWebJarsClasspath().getFiles(), getOutputDirectory().get().getAsFile());

        workerExecutor.submit(WebJarsExtractRunnable.class, workerConfiguration -> {
            workerConfiguration.setIsolationMode(IsolationMode.PROCESS);
            workerConfiguration.forkOptions(options -> options.jvmArgs("-XX:MaxMetaspaceSize=256m"));
            workerConfiguration.params(spec);
            workerConfiguration.classpath(webJarsExtractorClasspath);
            workerConfiguration.setDisplayName("Extracting WebJars");
        });

        workerExecutor.await();
    }
}
