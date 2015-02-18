package org.iatoki.judgels.gabriel.blackbox;

public enum GeneralVerdict implements BlackBoxVerdict {
    PENDING {
        @Override
        public String getCode() {
            return "?";
        }

        @Override
        public String getDescription() {
            return "Pending";
        }
    },

    INTERNAL_ERROR {
        @Override
        public String getCode() {
            return "!!!";
        }

        @Override
        public String getDescription() {
            return "Internal Error";
        }
    }
}
