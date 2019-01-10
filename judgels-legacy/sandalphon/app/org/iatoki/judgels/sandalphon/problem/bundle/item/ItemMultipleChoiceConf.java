package org.iatoki.judgels.sandalphon.problem.bundle.item;

import java.util.List;

public final class ItemMultipleChoiceConf implements BundleItemConf {

    public String statement;
    public Double score;
    public Double penalty;
    public List<ItemChoice> choices;
}
