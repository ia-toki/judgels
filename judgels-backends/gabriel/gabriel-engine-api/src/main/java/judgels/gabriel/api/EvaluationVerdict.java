package judgels.gabriel.api;

public enum EvaluationVerdict implements NormalVerdict {
    OK {
        @Override
        public Verdict getVerdict() {
            return Verdicts.OK;
        }
    },

    RUNTIME_ERROR {
        @Override
        public Verdict getVerdict() {
            return Verdicts.RUNTIME_ERROR;
        }
    },

    TIME_LIMIT_EXCEEDED {
        @Override
        public Verdict getVerdict() {
            return Verdicts.TIME_LIMIT_EXCEEDED;
        }
    },

    SKIPPED {
        @Override
        public Verdict getVerdict() {
            return Verdicts.SKIPPED;
        }
    }
}
