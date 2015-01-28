package org.iatoki.judgels.gabriel.blackbox;

public enum ScoringVerdict implements NormalVerdict {
    WRONG_ANSWER {
        @Override
        public String getAbbreviatedName() {
            return "WA";
        }

        @Override
        public String getName() {
            return "Wrong Answer";
        }
    },

    ACCEPTED {
        @Override
        public String getAbbreviatedName() {
            return "AC";
        }

        @Override
        public String getName() {
            return "Accepted";
        }
    },

    OK {
        @Override
        public String getAbbreviatedName() {
            return "OK";
        }

        @Override
        public String getName() {
            return "OK";
        }
    }
}
