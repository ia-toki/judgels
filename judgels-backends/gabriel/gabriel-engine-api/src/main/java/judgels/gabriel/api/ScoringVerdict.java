package judgels.gabriel.api;

public enum ScoringVerdict implements NormalVerdict {
    ACCEPTED {
        @Override
        public Verdict getVerdict() {
            return Verdicts.ACCEPTED;
        }
    },

    OK {
        @Override
        public Verdict getVerdict() {
            return Verdicts.OK;
        }
    },

    WRONG_ANSWER {
        @Override
        public Verdict getVerdict() {
            return Verdicts.WRONG_ANSWER;
        }
    }
}
