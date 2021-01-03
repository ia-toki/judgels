package judgels.uriel.persistence;

public interface ContestRoleDao {
    boolean isViewerOrAbove(String userJid, String contestJid);
    boolean isContestant(String userJid, String contestJid);
    boolean isSupervisorOrAbove(String userJid, String contestJid);
    boolean isManager(String userJid, String contestJid);
    void invalidateCaches(String userJid, String contestJid);
    void invalidateCaches();
}
