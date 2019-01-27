package org.iatoki.judgels.jophiel.user;

import judgels.persistence.Model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractUserModel extends Model {
    public String userJid;

    @Column(columnDefinition = "TEXT")
    public String accessToken;

    @Column(columnDefinition = "TEXT")
    public String refreshToken;

    @Column(columnDefinition = "TEXT")
    public String idToken;

    public long expirationTime;
}
