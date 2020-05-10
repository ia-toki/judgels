package org.iatoki.judgels.play.jid;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public abstract class AbstractBaseJidCacheServiceImpl<M extends AbstractJidCacheModel> implements BaseJidCacheService<M> {

    private final BaseJidCacheDao<M> jidCacheDao;

    public AbstractBaseJidCacheServiceImpl(BaseJidCacheDao<M> jidCacheDao) {
        this.jidCacheDao = jidCacheDao;
    }

    @Override
    public final void putDisplayName(String jid, String displayName, String user, String ipAddress) {
        if (jidCacheDao.existsByJid(jid)) {
            editDisplayName(jid, displayName, user, ipAddress);
        } else {
            createDisplayName(jid, displayName, user, ipAddress);
        }
    }

    @Override
    public final String getDisplayName(String jid) {

        if (!jidCacheDao.existsByJid(jid)) {
            return jid;
        } else {
            M jidCacheModel = jidCacheDao.findByJid(jid);
            return jidCacheModel.displayName;
        }
    }

    @Override
    public final Map<String, String> getDisplayNames(List<String> jids) {
        List<M> entries = jidCacheDao.getByJids(jids);

        Map<String, String> displayNamesMap = Maps.newHashMap();

        for (M entry : entries) {
            displayNamesMap.put(entry.jid, entry.displayName);
        }

        for (String jid : jids) {
            if (!displayNamesMap.containsKey(jid)) {
                displayNamesMap.put(jid, jid);
            }
        }

        return ImmutableMap.copyOf(displayNamesMap);
    }

    private void createDisplayName(String jid, String displayName, String user, String ipAddress) {
        M jidCacheModel = jidCacheDao.createJidCacheModel();

        jidCacheModel.jid = jid;
        jidCacheModel.displayName = displayName;

        jidCacheDao.persist(jidCacheModel, user, ipAddress);
    }

    private void editDisplayName(String jid, String displayName, String user, String ipAddress) {
        M jidCacheModel = jidCacheDao.findByJid(jid);

        jidCacheModel.jid = jid;
        jidCacheModel.displayName = displayName;

        jidCacheDao.edit(jidCacheModel, user, ipAddress);
    }
}
