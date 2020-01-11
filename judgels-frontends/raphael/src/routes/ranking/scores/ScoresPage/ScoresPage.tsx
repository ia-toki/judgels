import { HTMLTable } from '@blueprintjs/core';
import { parse } from 'query-string';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { Card } from '../../../../components/Card/Card';
import Pagination from '../../../../components/Pagination/Pagination';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { UserTopStatsResponse } from '../../../../modules/api/jerahmeel/user';
import { rankingActions } from '../../modules/rankingActions';

import './ScoresPage.css';

interface ScoresPageProps extends RouteComponentProps<{}> {
  onGetTopUserStats: (page?: number, pageSize?: number) => Promise<UserTopStatsResponse>;
  onAppendRoute: (queries: any) => any;
}

interface ScoresPageState {
  response?: UserTopStatsResponse;
}

class ScoresPage extends React.Component<ScoresPageProps, ScoresPageState> {
  private static PAGE_SIZE = 100;

  state: ScoresPageState = {};

  render() {
    return (
      <Card title="Top scorers">
        {this.renderScores()}
        <Pagination currentPage={1} pageSize={ScoresPage.PAGE_SIZE} onChangePage={this.onChangePage} />
      </Card>
    );
  }

  private renderScores = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data, profilesMap } = response;

    const page = +(parse(this.props.location.search).page || '1');
    const baseRank = (page - 1) * ScoresPage.PAGE_SIZE + 1;
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

  private onChangePage = async (nextPage: number) => {
    const response = await this.props.onGetTopUserStats(nextPage, ScoresPage.PAGE_SIZE);
    this.setState({ response });
    return response.data.totalCount;
  };
}

const mapDispatchToProps = {
  onGetTopUserStats: rankingActions.getTopUserStats,
};

export default withRouter<any, any>(connect(undefined, mapDispatchToProps)(ScoresPage));
