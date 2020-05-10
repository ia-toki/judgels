package org.iatoki.judgels.sandalphon.problem.bundle.item;

public interface BundleItemHasScore {

    double calculateScore(BundleItemConf conf, String answer);
}
