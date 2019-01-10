package org.iatoki.judgels.sandalphon.grader;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.iatoki.judgels.play.api.JudgelsAppClient;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public final class GraderServiceImpl implements GraderService {

    private final GraderDao graderDao;

    @Inject
    public GraderServiceImpl(GraderDao graderDao) {
        this.graderDao = graderDao;
    }

    @Override
    public boolean graderExistsByJid(String graderJid) {
        return graderDao.existsByJid(graderJid);
    }

    @Override
    public Grader findGraderById(long graderId) throws GraderNotFoundException {
        GraderModel graderModel = graderDao.findById(graderId);
        if (graderModel == null) {
            throw new GraderNotFoundException("Grader not found.");
        }

        return createGraderFromModel(graderModel);
    }

    @Override
    public Grader findGraderByJid(String graderJid) {
        GraderModel graderModel = graderDao.findByJid(graderJid);

        return createGraderFromModel(graderModel);
    }

    @Override
    public Grader createGrader(String name, String userJid, String userIpAddress) {
        GraderModel graderModel = new GraderModel();
        graderModel.name = name;
        graderModel.secret = JudgelsPlayUtils.generateNewSecret();

        graderDao.persist(graderModel, userJid, userIpAddress);

        return createGraderFromModel(graderModel);
    }

    @Override
    public void updateGrader(String graderJid, String name, String userJid, String userIpAddress) {
        GraderModel graderModel = graderDao.findByJid(graderJid);
        graderModel.name = name;

        graderDao.edit(graderModel, userJid, userIpAddress);
    }

    @Override
    public Page<Grader> getPageOfGraders(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = graderDao.countByFilters(filterString, ImmutableMap.of(), ImmutableMap.of());
        List<GraderModel> graderModels = graderDao.findSortedByFilters(orderBy, orderDir, filterString, pageIndex * pageSize, pageSize);

        List<Grader> graders = Lists.transform(graderModels, m -> createGraderFromModel(m));

        return new Page<>(graders, totalPages, pageIndex, pageSize);
    }

    @Override
    public boolean clientExistsByJid(String clientJid) {
        return graderExistsByJid(clientJid);
    }

    @Override
    public JudgelsAppClient findClientByJid(String clientJid) {
        return findGraderByJid(clientJid);
    }

    private static Grader createGraderFromModel(GraderModel graderModel) {
        return new Grader(graderModel.id, graderModel.jid, graderModel.name, graderModel.secret);
    }
}
