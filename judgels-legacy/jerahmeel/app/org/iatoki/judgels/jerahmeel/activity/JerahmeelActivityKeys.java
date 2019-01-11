package org.iatoki.judgels.jerahmeel.activity;

import org.iatoki.judgels.jophiel.activity.ThreeEntityActivityKey;

public final class JerahmeelActivityKeys {

    public static final ThreeEntityActivityKey SUBMIT = new ThreeEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "SUBMIT";
        }

        @Override
        public String toString() {
            return "submit " + getEntity() + " " + getEntityName() + " for " + getRefEntity() + " " + getRefEntityName() + " in " + getRefRefEntity() + " " + getRefRefEntityName() + ".";
        }
    };

    public static final ThreeEntityActivityKey REGRADE = new ThreeEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "REGRADE";
        }

        @Override
        public String toString() {
            return "regrade " + getEntity() + " " + getEntityName() + " in " + getRefEntity() + " " + getRefEntityName() + " in " + getRefRefEntity() + " " + getRefRefEntityName() + ".";
        }
    };

    private JerahmeelActivityKeys() {
        // prevent instantiation
    }
}
