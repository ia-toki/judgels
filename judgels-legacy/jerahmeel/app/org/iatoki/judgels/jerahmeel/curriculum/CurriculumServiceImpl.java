package org.iatoki.judgels.jerahmeel.curriculum;

import com.google.common.collect.Lists;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class CurriculumServiceImpl implements CurriculumService {

    private final CurriculumDao curriculumDao;

    @Inject
    public CurriculumServiceImpl(CurriculumDao curriculumDao) {
        this.curriculumDao = curriculumDao;
    }

    @Override
    public List<Curriculum> getAllCurriculums() {
        List<CurriculumModel> curriculumModels = curriculumDao.getAll();

        return curriculumModels.stream().map(m -> CurriculumServiceUtils.createCurriculumFromModel(m)).collect(Collectors.toList());
    }

    @Override
    public Page<Curriculum> getPageOfCurriculums(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = curriculumDao.countByFilters(filterString);
        List<CurriculumModel> curriculumModels = curriculumDao.findSortedByFilters(orderBy, orderDir, filterString, pageIndex * pageSize, pageSize);

        List<Curriculum> curriculums = Lists.transform(curriculumModels, m -> CurriculumServiceUtils.createCurriculumFromModel(m));

        return new Page<>(curriculums, totalPages, pageIndex, pageSize);
    }

    @Override
    public Curriculum findCurriculumById(long curriculumId) throws CurriculumNotFoundException {
        CurriculumModel curriculumModel = curriculumDao.findById(curriculumId);
        if (curriculumModel != null) {
            return CurriculumServiceUtils.createCurriculumFromModel(curriculumModel);
        } else {
            throw new CurriculumNotFoundException("Curriculum not found.");
        }
    }

    @Override
    public Curriculum createCurriculum(String name, String description, String userJid, String userIpAddress) {
        CurriculumModel curriculumModel = new CurriculumModel();
        curriculumModel.name = name;
        curriculumModel.description = description;

        curriculumDao.persist(curriculumModel, userJid, userIpAddress);

        return CurriculumServiceUtils.createCurriculumFromModel(curriculumModel);
    }

    @Override
    public void updateCurriculum(String curriculumJid, String name, String description, String userJid, String userIpAddress) {
        CurriculumModel curriculumModel = curriculumDao.findByJid(curriculumJid);
        curriculumModel.name = name;
        curriculumModel.description = description;

        curriculumDao.edit(curriculumModel, userJid, userIpAddress);
    }
}
