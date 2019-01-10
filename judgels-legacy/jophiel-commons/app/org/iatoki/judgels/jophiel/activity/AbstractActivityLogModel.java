package org.iatoki.judgels.jophiel.activity;

import org.iatoki.judgels.play.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractActivityLogModel extends AbstractModel {

    @Id
    @GeneratedValue
    public long id;

    public String username;

    public String keyAction;

    @Column(columnDefinition = "TEXT")
    public String parameters;

    public long time;
}
