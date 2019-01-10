package org.iatoki.judgels.jophiel.avatar;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public abstract class AbstractBaseAvatarCacheServiceImpl<M extends AbstractAvatarCacheModel> implements BaseAvatarCacheService<M> {

    private final BaseAvatarCacheDao<M> avatarCacheDao;

    public AbstractBaseAvatarCacheServiceImpl(BaseAvatarCacheDao<M> avatarCacheDao) {
        this.avatarCacheDao = avatarCacheDao;
    }

    @Override
    public final void putImageUrl(String userJid, String imageUrl, String user, String ipAddress) {
        if (avatarCacheDao.existsByUserJid(userJid)) {
            editImageUrl(userJid, imageUrl, user, ipAddress);
        } else {
            createImageUrl(userJid, imageUrl, user, ipAddress);
        }
    }

    @Override
    public final URL getAvatarUrl(String userJid, String defaultAvatarUrl) {
        try {
            if (!avatarCacheDao.existsByUserJid(userJid)) {
                return new URL(defaultAvatarUrl);
            } else {
                M jidCacheModel = avatarCacheDao.findByUserJid(userJid);
                return new URL(jidCacheModel.avatarUrl == null ? defaultAvatarUrl : jidCacheModel.avatarUrl);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final Map<String, URL> getAvatarUrls(List<String> userJids, String defaultAvatarUrl) {
        try {
            List<M> entries = avatarCacheDao.findByUserJids(userJids);

            Map<String, URL> displayNamesMap = Maps.newHashMap();

            for (M entry : entries) {
                displayNamesMap.put(entry.userJid, new URL(entry.avatarUrl));
            }

            for (String jid : userJids) {
                if (!displayNamesMap.containsKey(jid)) {
                    displayNamesMap.put(jid, new URL(defaultAvatarUrl));
                }
            }

            return ImmutableMap.copyOf(displayNamesMap);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createImageUrl(String userJid, String imageUrl, String user, String ipAddress) {
        M avatarCacheModel = avatarCacheDao.createAvatarCacheModel();

        avatarCacheModel.userJid = userJid;
        avatarCacheModel.avatarUrl = imageUrl;

        avatarCacheDao.persist(avatarCacheModel, user, ipAddress);
    }

    private void editImageUrl(String userJid, String imageUrl, String user, String ipAddress) {
        M avatarCacheModel = avatarCacheDao.findByUserJid(userJid);

        avatarCacheModel.userJid = userJid;
        avatarCacheModel.avatarUrl = imageUrl;

        avatarCacheDao.edit(avatarCacheModel, user, ipAddress);
    }
}
