package judgels.gabriel.api;

public enum CompilationVerdict implements EngineVerdict {
    OK {
        @Override
        public Verdict getVerdict() {
            return Verdicts.OK;
        }
    },

    COMPILATION_ERROR {
        @Override
        public Verdict getVerdict() {
            return Verdicts.COMPILATION_ERROR;
        }
    }
}
