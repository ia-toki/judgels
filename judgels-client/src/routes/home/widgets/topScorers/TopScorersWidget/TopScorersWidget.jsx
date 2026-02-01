import { HTMLTable } from '@blueprintjs/core';
import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';

import { Card } from '../../../../../components/Card/Card';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { UserRef } from '../../../../../components/UserRef/UserRef';

import * as widgetActions from '../../modules/widgetActions';

import './TopScorersWidget.scss';

export default function TopScorersWidget() {
  const dispatch = useDispatch();

  const [state, setState] = useState({
    response: undefined,
  });

  const refreshTopUserStats = async () => {
    const response = await getTopUserStats(1, 5);
    setState({ response });
  };

  useEffect(() => {
    refreshTopUserStats();
  }, []);

  const render = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    return (
      <Card className="top-scorers-widget" title="Top scorers">
        {renderTable()}
      </Card>
    );
  };

  const renderTable = () => {
    const { data, profilesMap } = state.response;
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

  return render();
}
