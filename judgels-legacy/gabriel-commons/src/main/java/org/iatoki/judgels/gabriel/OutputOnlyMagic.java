package org.iatoki.judgels.gabriel;

import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

/*
 * This class exists to collect all magic constants required for 2 full-time developers to finish
 * the output-only feature in just 3 nights.
 *
 * Forgive us, God.
 *
 * - Jordan & Ashar
 */
public final class OutputOnlyMagic {
    public static final String KEY = "OutputOnly";
    public static final String DISPLAY_NAME = "-";
    public static final GradingLanguage LANGUAGE = new OutputOnlyGradingLanguage();

    public static class OutputOnlyGradingLanguage extends AbstractGradingLanguage {
        @Override
        public String getName() {
            return DISPLAY_NAME;
        }

        @Override
        public List<String> getCompilationCommand(String sourceFilename) {
            return null;
        }

        @Override
        public String getExecutableFilename(String sourceFilename) {
            return null;
        }

        @Override
        public List<String> getExecutionCommand(String sourceFilename) {
            return null;
        }

        @Override
        protected Set<String> getAllowedExtensions() {
            return ImmutableSet.of("zip");
        }
    }

    private OutputOnlyMagic() {

    }
}
