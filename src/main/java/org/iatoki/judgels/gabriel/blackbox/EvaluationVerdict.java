package org.iatoki.judgels.gabriel.blackbox;

public enum EvaluationVerdict implements NormalVerdict {
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

    RUNTIME_ERROR {
        @Override
        public String getCode() {
            return "RTE";
        }

        @Override
        public String getDescription() {
            return "Runtime Error";
        }
    },

    TIME_LIMIT_EXCEEDED {
        @Override
        public String getCode() {
            return "TLE";
        }

        @Override
        public String getDescription() {
            return "Time Limit Exceeded";
        }
    },

    SKIPPED {
        @Override
        public String getCode() {
            return "SKP";
        }

        @Override
        public String getDescription() {
            return "Skipped";
        }
    };
}
