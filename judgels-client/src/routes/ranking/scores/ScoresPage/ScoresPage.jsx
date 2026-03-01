import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';
import { useLocation } from '@tanstack/react-router';

import { Card } from '../../../../components/Card/Card';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../components/Pagination/Pagination';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { topUserStatsQueryOptions } from '../../../../modules/queries/stats';

import './ScoresPage.scss';

const PAGE_SIZE = 50;

export default function ScoresPage() {
  const location = useLocation();
  const page = location.search.page;

  const { data: response } = useQuery(topUserStatsQueryOptions({ page, pageSize: PAGE_SIZE }));

  const renderScores = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data, profilesMap } = response;

    const baseRank = (page - 1) * PAGE_SIZE + 1;
    const rows = data.page.map((e, idx) => (
      <tr key={e.userJid}>
        <td className="col-rank">{baseRank + idx}</td>
        <td>
          <UserRef profile={profilesMap[e.userJid]} showFlag />
        </td>
        <td>{e.totalScores}</td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list scores-page-table">
        <thead>
          <tr>
            <th className="col-rank">#</th>
            <th>User</th>
            <th>Score</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  return (
    <Card title="Top scorers">
      {renderScores()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </Card>
  );
}
