package judgels.play;

import judgels.play.tasks.WebJarsExtract;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.playframework.tasks.PlayRun;

import static org.gradle.api.plugins.BasePlugin.ASSEMBLE_TASK_NAME;
import static org.gradle.playframework.plugins.PlayApplicationPlugin.ASSETS_JAR_TASK_NAME;

public class PlayWebJarsPlugin implements Plugin<Project> {
    public static final String WEBJAR_CONFIGURATION_NAME = "webJar";
    public static final String WEBJARS_EXTRACTOR_CONFIGURATION_NAME = "webJarsExtractor";
    public static final String WEBJARS_EXTRACT_TASK_NAME = "extractPlayWebJars";

    @Override
    public void apply(Project project) {
        createWebJarConfiguration(project);
        Configuration webJarsExtractorConfiguration = createWebJarsExtractorConfiguration(project);
        createDefaultWebJarsExtractTask(project, webJarsExtractorConfiguration);
        configureWebJarsExtractTask(project);
    }

    private void createWebJarConfiguration(Project project) {
        Configuration configuration = project.getConfigurations().create(WEBJAR_CONFIGURATION_NAME);
        configuration.setTransitive(true);
    }

    private Configuration createWebJarsExtractorConfiguration(Project project) {
        Configuration configuration = project.getConfigurations().create(WEBJARS_EXTRACTOR_CONFIGURATION_NAME);
        configuration.setVisible(false);
        configuration.setTransitive(true);
        configuration.setDescription("The WebJars extractor library used to extract WebJars.");
        return configuration;
    }

    private void createDefaultWebJarsExtractTask(Project project, Configuration configuration) {
        project.getTasks().register(WEBJARS_EXTRACT_TASK_NAME, WebJarsExtract.class, task -> {
            task.setDescription("Extracts WebJars.");
            task.getOutputDirectory().set(project.getLayout().getBuildDirectory().dir("src/play/webJars"));
            task.getWebJarsClasspath().setFrom(project.getConfigurations().getByName(WEBJAR_CONFIGURATION_NAME));
            task.getWebJarsExtractorClasspath().setFrom(configuration);
        });
    }

    private void configureWebJarsExtractTask(Project project) {
        TaskProvider<WebJarsExtract> webJarsExtractTaskProvider = project.getTasks().named(WEBJARS_EXTRACT_TASK_NAME, WebJarsExtract.class);

        project.getTasks().named(ASSEMBLE_TASK_NAME, task -> {
            task.dependsOn(webJarsExtractTaskProvider);
        });
        project.getTasks().named(ASSETS_JAR_TASK_NAME, Jar.class, task -> {
            task.dependsOn(webJarsExtractTaskProvider);
            task.from(webJarsExtractTaskProvider.get().getOutputDirectory(), copySpec -> copySpec.into("public"));
        });
        project.getTasks().named("runPlay", PlayRun.class, task -> {
            task.getAssetsDirs().from(webJarsExtractTaskProvider.get().getOutputDirectory());
        });
    }
}
