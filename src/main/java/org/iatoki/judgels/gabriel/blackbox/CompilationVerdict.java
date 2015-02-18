package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.Verdict;

public enum CompilationVerdict implements BlackBoxVerdict {
    OK {
        @Override
        public String getCode() {
            return "OK";
        }

        @Override
        public String getDescription() {
            return "OK";
        }
    },

    COMPILATION_ERROR {
        @Override
        public String getCode() {
            return "CE";
        }

        @Override
        public String getDescription() {
            return "Compilation Error";
        }
    }
}
