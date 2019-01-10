package org.iatoki.judgels.jophiel.activity;

public interface ActivityKey {

    ActivityKey fromJson(String json);

    String getKeyAction();

    String toJsonString();
}
