package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

import java.io.IOException;
import java.util.List;

@ImplementedBy(BundleItemServiceImpl.class)
public interface BundleItemService {

    boolean bundleItemExistsInProblemWithCloneByJid(String problemJid, String userJid, String itemJid) throws IOException;

    boolean bundleItemExistsInProblemWithCloneByMeta(String problemJid, String userJid, String meta) throws IOException;

    BundleItem findInProblemWithCloneByItemJid(String problemJid, String userJid, String itemJid) throws IOException;

    String getItemConfInProblemWithCloneByJid(String problemJid, String userJid, String itemJid, String languageCode) throws IOException;

    Page<BundleItem> getPageOfBundleItemsInProblemWithClone(String problemJid, String userJid, long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) throws IOException;

    List<BundleItem> getBundleItemsInProblemWithClone(String problemJid, String userJid) throws IOException;

    void createBundleItem(String problemJid, String userJid, BundleItemType itemType, String meta, String conf, String languageCode) throws IOException;

    void updateBundleItem(String problemJid, String userJid, String itemJid, String meta, String conf, String languageCode) throws IOException;

    void moveBundleItemUp(String problemJid, String userJid, String itemJid) throws IOException;

    void moveBundleItemDown(String problemJid, String userJid, String itemJid) throws IOException;

    void removeBundleItem(String problemJid, String userJid, String itemJid) throws IOException;
}
