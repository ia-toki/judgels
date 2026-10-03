package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.model.ContestStyleModel;
import judgels.persistence.model.ContestStyleModel_;

public class ContestStyleDao extends Dao<ContestStyleModel> {
    @Inject
    public ContestStyleDao(DaoData data) {
        super(data);
    }

    public Optional<ContestStyleModel> selectByContestJid(String contestJid) {
        return select()
                .where(columnEq(ContestStyleModel_.contestJid, contestJid))
                .unique();
    }
}
