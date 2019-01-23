package judgels.gabriel.api;

public enum GeneralVerdict implements EngineVerdict {
    PENDING {
        @Override
        public Verdict getVerdict() {
            return Verdicts.PENDING;
        }
    },

    INTERNAL_ERROR {
        @Override
        public Verdict getVerdict() {
            return Verdicts.INTERNAL_ERROR;
        }
    }
}
