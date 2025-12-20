import { HTMLTable } from '@blueprintjs/core';
import { parse } from 'query-string';
import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useLocation } from 'react-router-dom';

import { Card } from '../../../../components/Card/Card';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../components/Pagination/Pagination';
import { UserRef } from '../../../../components/UserRef/UserRef';

import * as rankingActions from '../../modules/rankingActions';

import './ScoresPage.scss';

const PAGE_SIZE = 50;

export default function ScoresPage() {
  const location = useLocation();
  const dispatch = useDispatch();

  const [state, setState] = useState({
    response: undefined,
  });

  const render = () => {
    return (
      <Card title="Top scorers">
        {renderScores()}
        <Pagination pageSize={PAGE_SIZE} onChangePage={onChangePage} />
      </Card>
    );
  };

  const renderScores = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    const { data, profilesMap } = response;

    const page = +(parse(location.search).page || '1');
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

  const onChangePage = async nextPage => {
    const response = await dispatch(rankingActions.getTopUserStats(nextPage, PAGE_SIZE));
    setState({ response });
    return response.data.totalCount;
  };

  return render();
}
