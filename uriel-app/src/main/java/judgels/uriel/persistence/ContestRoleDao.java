package judgels.uriel.persistence;

public interface ContestRoleDao {
    boolean isViewerOrAbove(String userJid, String contestJid);
    boolean isManager(String userJid, String contestJid);
}
