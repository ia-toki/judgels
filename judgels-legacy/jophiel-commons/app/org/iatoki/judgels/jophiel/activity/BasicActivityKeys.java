package org.iatoki.judgels.jophiel.activity;

public final class BasicActivityKeys {

    public static final OneEntityActivityKey CREATE = new OneEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "CREATE";
        }

        @Override
        public String toString() {
            return "create " + getEntity() + " " + getEntityName() + ".";
        }
    };

    public static final OneEntityActivityKey ADD = new OneEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "ADD";
        }

        @Override
        public String toString() {
            return "add " + getEntity() + " " + getEntityName() + ".";
        }
    };

    public static final TwoEntityActivityKey ADD_IN = new TwoEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "ADD_IN";
        }

        @Override
        public String toString() {
            return "add " + getEntity() + " " + getEntityName() + " in " + getRefEntity() + " " + getRefEntityName() +  ".";
        }
    };

    public static final ThreeEntityActivityKey ADD_TO_IN = new ThreeEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "ADD_TO_IN";
        }

        @Override
        public String toString() {
            return "add " + getEntity() + " " + getEntityName() + " to " + getRefEntity() + " " + getRefEntityName() + " in " + getRefRefEntity() + " " + getRefRefEntityName() + ".";
        }
    };

    public static final OneEntityActivityKey UPLOAD = new OneEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "UPLOAD";
        }

        @Override
        public String toString() {
            return "upload " + getEntity() + " " + getEntityName() + ".";
        }
    };

    public static final TwoEntityActivityKey UPLOAD_IN = new TwoEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "UPLOAD_IN";
        }

        @Override
        public String toString() {
            return "upload " + getEntity() + " " + getEntityName() + " in " + getRefEntity() + " " + getRefEntityName() +  ".";
        }
    };

    public static final ThreeEntityActivityKey UPLOAD_TO_IN = new ThreeEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "UPLOAD_TO_IN";
        }

        @Override
        public String toString() {
            return "upload " + getEntity() + " " + getEntityName() + " to " + getRefEntity() + " " + getRefEntityName() + " in " + getRefRefEntity() + " " + getRefRefEntityName() + ".";
        }
    };

    public static final RenameEntityActivityKey RENAME = new RenameEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "RENAME";
        }

        @Override
        public String toString() {
            return "rename " + getEntity() + " " + getEntityFromName() + " to " + getEntityToName() + ".";
        }
    };

    public static final RenameInEntityActivityKey RENAME_IN = new RenameInEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "RENAME_IN";
        }

        @Override
        public String toString() {
            return "rename " + getEntity() + " " + getEntityFromName() + " to " + getEntityToName() + " in " + getRefEntity() + " " + getRefEntityName() + ".";
        }
    };

    public static final OneEntityActivityKey EDIT = new OneEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "EDIT";
        }

        @Override
        public String toString() {
            return "edit " + getEntity() + " " + getEntityName() + ".";
        }
    };

    public static final TwoEntityActivityKey EDIT_IN = new TwoEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "EDIT_IN";
        }

        @Override
        public String toString() {
            return "edit " + getEntity() + " " + getEntityName() + " in " + getRefEntity() + " " + getRefEntityName() +  ".";
        }
    };

    public static final OneEntityActivityKey REMOVE = new OneEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "REMOVE";
        }

        @Override
        public String toString() {
            return "remove " + getEntity() + " " + getEntityName() + ".";
        }
    };

    public static final TwoEntityActivityKey REMOVE_FROM = new TwoEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "REMOVE_FROM";
        }

        @Override
        public String toString() {
            return "remove " + getEntity() + " " + getEntityName() + " from " + getRefEntity() + " " + getRefEntityName() +  ".";
        }
    };

    public static final ThreeEntityActivityKey REMOVE_FROM_IN = new ThreeEntityActivityKey() {
        @Override
        public String getKeyAction() {
            return "REMOVE_FROM_IN";
        }

        @Override
        public String toString() {
            return "remove " + getEntity() + " " + getEntityName() + " from " + getRefEntity() + " " + getRefEntityName() + " in " + getRefRefEntity() + " " + getRefRefEntityName() + ".";
        }
    };

    private BasicActivityKeys() {
        // prevent instantiation
    }
}
