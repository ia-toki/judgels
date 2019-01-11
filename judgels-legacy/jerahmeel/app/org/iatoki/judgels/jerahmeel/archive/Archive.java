package org.iatoki.judgels.jerahmeel.archive;

import java.util.List;

public final class Archive {

    private final long id;
    private final String jid;
    private final Archive parentArchive;
    private final List<Archive> subArchives;
    private final String name;
    private final String description;

    public Archive(long id, String jid, Archive parentArchive, List<Archive> subArchives, String name, String description) {
        this.id = id;
        this.jid = jid;
        this.parentArchive = parentArchive;
        this.subArchives = subArchives;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getJid() {
        return jid;
    }

    public Archive getParentArchive() {
        return parentArchive;
    }

    public List<Archive> getSubArchives() {
        return subArchives;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean containsJidInHierarchy(String archiveJid) {
        Archive archive = this;
        while (archive != null) {
            if (archive.getJid().equals(archiveJid)) {
                return true;
            }
            archive = archive.getParentArchive();
        }
        return false;
    }

    public String prependSpacesBasedOnLevel(int totalSpaces) {
        int depth = 0;
        Archive parent = getParentArchive();
        while (parent != null) {
            depth++;
            parent = parent.getParentArchive();
        }

        StringBuilder sb = new StringBuilder();
        while (depth != 0) {
            for (int i = 0; i < totalSpaces; ++i) {
                sb.append("&nbsp;");
            }
            depth--;
        }
        sb.append(getName());

        return sb.toString();
    }
}
