import { HTMLTable } from '@blueprintjs/core';
import { Box } from '@blueprintjs/labs';
import { useQuery } from '@tanstack/react-query';

import { Card } from '../../../../../components/Card/Card';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { UserRef } from '../../../../../components/UserRef/UserRef';
import { trainingTopUserStatsQueryOptions } from '../../../../../modules/queries/trainingUserStats';

import './TopScorersWidget.scss';

export default function TopScorersWidget() {
  const { data: response } = useQuery(trainingTopUserStatsQueryOptions({ page: 1, pageSize: 5 }));

  if (!response) {
    return <LoadingState />;
  }

  const renderTable = () => {
    const { data, profilesMap } = response;
    if (data.page.length === 0) {
      return (
        <Box padding={5}>
          <small>No data yet.</small>
        </Box>
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
