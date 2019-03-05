package judgels.sandalphon.api.problem.bundle;

public enum ItemType {
    STATEMENT, MULTIPLE_CHOICE;

    // String constants for use in annotations (evaluated at compile-time)
    static final String Statement = "STATEMENT";
    static final String MultipleChoice = "MULTIPLE_CHOICE";
}
