package org.iatoki.judgels.gabriel.blackbox;

public enum ScoringVerdict implements NormalVerdict {
    ACCEPTED {
        @Override
        public String getCode() {
            return "AC";
        }

        @Override
        public String getName() {
            return "Accepted";
        }
    },

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

    WRONG_ANSWER {
        @Override
        public String getCode() {
            return "WA";
        }

        @Override
        public String getName() {
            return "Wrong Answer";
        }
    }
}
