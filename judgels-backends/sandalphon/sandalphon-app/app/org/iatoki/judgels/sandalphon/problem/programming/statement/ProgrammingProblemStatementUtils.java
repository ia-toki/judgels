package org.iatoki.judgels.sandalphon.problem.programming.statement;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public final class ProgrammingProblemStatementUtils {

    private static final Map<String, String> DEFAULT_TEXTS = ImmutableMap.of(
            "id-ID",
                    "<h3>Deskripsi</h3>\n"
                    + "\n"
                    + "<p>Lorem ipsum.</p>\n"
                    + "\n"
                    + "<h3>Format Masukan</h3>\n"
                    + "\n"
                    + "<p>Lorem ipsum.</p>\n"
                    + "\n"
                    + "<h3>Format Keluaran</h3>\n"
                    + "\n"
                    + "<p>Lorem ipsum.</p>\n"
                    + "\n"
                    + "<h3>Contoh Masukan</h3>\n"
                    + "\n"
                    + "<pre>\n"
                    + "Lorem ipsum.</pre>\n"
                    + "\n"
                    + "<h3>Contoh Keluaran</h3>\n"
                    + "\n"
                    + "<pre>\n"
                    + "Lorem ipsum.</pre>\n"
                    + "\n"
                    + "<h3>Subsoal</h3>\n"
                    + "\n"
                    + "<h4>Subsoal 1</h4>\n"
                    + "\n"
                    + "<ul>\n"
                    + "\t<li>1 &le; N &le; 100</li>\n"
                    + "</ul>\n",
            "en-US",
                    "<h3>Description</h3>\n"
                    + "\n"
                    + "<p>Lorem ipsum.</p>\n"
                    + "\n"
                    + "<h3>Input Format</h3>\n"
                    + "\n"
                    + "<p>Lorem ipsum.</p>\n"
                    + "\n"
                    + "<h3>Output Format</h3>\n"
                    + "\n"
                    + "<p>Lorem ipsum.</p>\n"
                    + "\n"
                    + "<h3>Sample Input</h3>\n"
                    + "\n"
                    + "<pre>\n"
                    + "Lorem ipsum.</pre>\n"
                    + "\n"
                    + "<h3>Sample Output</h3>\n"
                    + "\n"
                    + "<pre>\n"
                    + "Lorem ipsum.</pre>\n"
                    + "\n"
                    + "<h3>Subtasks</h3>\n"
                    + "\n"
                    + "<h4>Subtask 1</h4>\n"
                    + "\n"
                    + "<ul>\n"
                    + "\t<li>1 &le; N &le; 100</li>\n"
                    + "</ul>\n"
    );

    private ProgrammingProblemStatementUtils() {
        // prevent instantiation
    }

    public static String getDefaultText(String languageCode) {
        if (DEFAULT_TEXTS.containsKey(languageCode)) {
            return DEFAULT_TEXTS.get(languageCode);
        } else {
            return DEFAULT_TEXTS.get("en-US");
        }
    }
}
