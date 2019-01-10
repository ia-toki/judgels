package org.iatoki.judgels.sandalphon.activity;

import org.iatoki.judgels.jophiel.activity.TwoEntityActivityKey;

public final class SandalphonActivityKeys {

    public static final TwoEntityActivityKey COMMIT = new TwoEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "COMMIT";
        }

        @Override
        public String toString() {
            return "commit " + getEntity() + " " + getEntityName() + " in " + getRefEntity() + " " + getRefEntityName() + ".";
        }
    };

    public static final TwoEntityActivityKey RESTORE = new TwoEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "RESTORE";
        }

        @Override
        public String toString() {
            return "restore " + getEntity() + " " + getRefEntityName() + " " + getEntityName() + " in " + getRefEntity() + ".";
        }
    };

    public static final TwoEntityActivityKey SUBMIT = new TwoEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "SUBMIT";
        }

        @Override
        public String toString() {
            return "submit " + getEntity() + " " + getEntityName() + " for " + getRefEntity() + " " + getRefEntityName() + ".";
        }
    };

    public static final TwoEntityActivityKey REGRADE = new TwoEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "REGRADE";
        }

        @Override
        public String toString() {
            return "regrade " + getEntity() + " " + getEntityName() + " in " + getRefEntity() + " " + getRefEntityName() + ".";
        }
    };

    private SandalphonActivityKeys() {
        // prevent instantiation
    }
}
