import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';

import { Card } from '../../../../../components/Card/Card';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { UserRef } from '../../../../../components/UserRef/UserRef';
import { topUserStatsQueryOptions } from '../../../../../modules/queries/stats';

import './TopScorersWidget.scss';

export default function TopScorersWidget() {
  const { data: response } = useQuery(topUserStatsQueryOptions({ page: 1, pageSize: 5 }));

  if (!response) {
    return <LoadingState />;
  }

  const renderTable = () => {
    const { data, profilesMap } = response;
    if (data.page.length === 0) {
      return (
        <div className="top-scorers-widget__empty">
          <small>No data yet.</small>
        </div>
      );
    }

    const rows = data.page.map((e, idx) => (
      <tr key={e.userJid}>
        <td className="col-rank">{idx + 1}</td>
        <td>
          <UserRef profile={profilesMap[e.userJid]} showFlag />
        </td>
        <td className="col-score">{e.totalScores}</td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list top-scorers-widget__table">
        <thead>
          <tr>
            <th className="col-rank">#</th>
            <th>User</th>
            <th className="col-score">Score</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  return (
    <Card className="top-scorers-widget" title="Top scorers">
      {renderTable()}
    </Card>
  );
}
