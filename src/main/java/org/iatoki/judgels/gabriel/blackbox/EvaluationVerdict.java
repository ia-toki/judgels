package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.SandboxExecutionStatus;

public enum EvaluationVerdict implements NormalVerdict {
    OK {
        @Override
        public String getCode() {
            return "OK";
        }

        @Override
        public String getName() {
            return "OK";
        }
    },

    RUNTIME_ERROR {
        @Override
        public String getCode() {
            return "RTE";
        }

        @Override
        public String getName() {
            return "Runtime Error";
        }
    },

    TIME_LIMIT_EXCEEDED {
        @Override
        public String getCode() {
            return "TLE";
        }

        @Override
        public String getName() {
            return "Time Limit Exceeded";
        }
    },

    MEMORY_LIMIT_EXCEEDED {
        @Override
        public String getCode() {
            return "MLE";
        }

        @Override
        public String getName() {
            return "Memory Limit Exceeded";
        }
    },

    SKIPPED {
        @Override
        public String getCode() {
            return "SKP";
        }

        @Override
        public String getName() {
            return "Skipped";
        }
    };

    static EvaluationVerdict fromSandboxExecutionStatus(SandboxExecutionStatus status) {
        switch (status) {
            case RUNTIME_ERROR:
                return EvaluationVerdict.RUNTIME_ERROR;
            case TIME_LIMIT_EXCEEDED:
                return EvaluationVerdict.TIME_LIMIT_EXCEEDED;
            case MEMORY_LIMIT_EXCEEDED:
                return EvaluationVerdict.MEMORY_LIMIT_EXCEEDED;
            default:
                return EvaluationVerdict.OK;
        }
    }
}
