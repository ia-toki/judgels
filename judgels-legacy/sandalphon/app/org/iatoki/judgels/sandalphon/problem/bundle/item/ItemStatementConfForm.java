package org.iatoki.judgels.sandalphon.problem.bundle.item;

import play.data.validation.Constraints;

public final class ItemStatementConfForm {

    @Constraints.Required
    public String meta;

    @Constraints.Required
    public String statement;
}
