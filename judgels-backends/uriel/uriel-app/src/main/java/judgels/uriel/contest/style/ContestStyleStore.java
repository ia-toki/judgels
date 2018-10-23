package judgels.uriel.contest.style;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.persistence.ContestStyleDao;
import judgels.uriel.persistence.ContestStyleModel;

public class ContestStyleStore {
    private final ContestStyleDao styleDao;
    private final ObjectMapper mapper;

    @Inject
    public ContestStyleStore(ContestStyleDao styleDao, ObjectMapper mapper) {
        this.styleDao = styleDao;
        this.mapper = mapper;
    }

    public void upsertIoiStyleConfig(String contestJid, IoiContestStyleConfig config) {
        upsertConfig(contestJid, config);
    }

    public void upsertIcpcStyleConfig(String contestJid, IcpcContestStyleConfig config) {
        upsertConfig(contestJid, config);
    }

    public ContestStyleConfig getStyleConfig(String contestJid) {
        return getConfig(contestJid, ContestStyleConfig.class).orElse(new ContestStyleConfig.Builder().build());
    }

    public IcpcContestStyleConfig getIcpcStyleConfig(String contestJid) {
        return getConfig(contestJid, IcpcContestStyleConfig.class).orElse(new IcpcContestStyleConfig.Builder().build());
    }

    public IoiContestStyleConfig getIoiStyleConfig(String contestJid) {
        return getConfig(contestJid, IoiContestStyleConfig.class).orElse(new IoiContestStyleConfig.Builder().build());
    }

    public void upsertConfig(String contestJid, Object config) {
        ContestStyleModel model = new ContestStyleModel();
        model.contestJid = contestJid;

        try {
            model.config = mapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        styleDao.insert(model);
    }

    private <T> Optional<T> getConfig(String contestJid, Class<T> configClass) {
        return styleDao.selectByContestJid(contestJid)
                .map(model -> parseConfig(model.config, configClass));
    }

    private <T> T parseConfig(String config, Class<T> clazz) {
        try {
            return mapper.readValue(config, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
