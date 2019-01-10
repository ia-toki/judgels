package org.iatoki.judgels.jophiel.avatar;

import org.iatoki.judgels.play.model.AbstractModel;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractAvatarCacheModel extends AbstractModel {

    @Id
    @GeneratedValue
    public long id;

    public String userJid;

    public String avatarUrl;
}
