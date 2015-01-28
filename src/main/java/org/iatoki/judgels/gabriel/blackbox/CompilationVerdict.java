package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.Verdict;

public enum CompilationVerdict implements BlackBoxVerdict {
    OK {
        @Override
        public String getAbbreviatedName() {
            return "OK";
        }

        @Override
        public String getName() {
            return "OK";
        }
    },

    COMPILATION_ERROR {
        @Override
        public String getAbbreviatedName() {
            return "CE";
        }

        @Override
        public String getName() {
            return "Compilation Error";
        }
    }
}
