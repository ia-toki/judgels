package judgels.gabriel.api;

public enum CompilationVerdict implements EngineVerdict {
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

    COMPILATION_ERROR {
        @Override
        public String getCode() {
            return "CE";
        }

        @Override
        public String getName() {
            return "Compilation Error";
        }
    }
}
