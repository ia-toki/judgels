package org.iatoki.judgels.jerahmeel.statistic.point;

import com.google.inject.ImplementedBy;

import java.util.List;

@ImplementedBy(PointStatisticServiceImpl.class)
public interface PointStatisticService {

    boolean pointStatisticExists();

    PointStatistic getLatestPointStatisticWithPagination(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    void updatePointStatistic(List<PointStatisticEntry> pointStatisticEntries, long time);
}
