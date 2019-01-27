package org.iatoki.judgels.jophiel.activity;

import judgels.persistence.Model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractActivityLogModel extends Model {
    public String username;

    public String keyAction;

    @Column(columnDefinition = "TEXT")
    public String parameters;

    public long time;
}
