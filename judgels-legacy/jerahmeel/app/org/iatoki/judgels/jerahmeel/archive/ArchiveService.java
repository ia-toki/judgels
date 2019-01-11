package org.iatoki.judgels.jerahmeel.archive;

import com.google.inject.ImplementedBy;

import java.util.List;

@ImplementedBy(ArchiveServiceImpl.class)
public interface ArchiveService {

    boolean archiveExistsByJid(String archiveJid);

    List<Archive> getAllArchives();

    List<Archive> getChildArchives(String parentJid);

    List<ArchiveWithScore> getChildArchivesWithScore(String parentJid, String userJid);

    Archive findArchiveById(long archiveId) throws ArchiveNotFoundException;

    Archive findArchiveByJid(String archiveJid);

    long createArchive(String parentJid, String name, String description, String userJid, String userIpAddress);

    void updateArchive(String archiveJid, String parentJid, String name, String description, String userJid, String userIpAddress);
}
