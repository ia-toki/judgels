package judgels.gabriel.api;

public enum GeneralVerdict implements EngineVerdict {
    PENDING {
        @Override
        public String getCode() {
            return "?";
        }

        @Override
        public String getName() {
            return "Pending";
        }
    },

    INTERNAL_ERROR {
        @Override
        public String getCode() {
            return "!!!";
        }

        @Override
        public String getName() {
            return "Internal Error";
        }
    }
}
