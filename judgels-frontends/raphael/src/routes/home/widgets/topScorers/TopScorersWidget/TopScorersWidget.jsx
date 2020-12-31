import { HTMLTable } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../../components/Card/Card';
import { UserRef } from '../../../../../components/UserRef/UserRef';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import * as widgetActions from '../../modules/widgetActions';

import './TopScorersWidget.css';

class TopScorersWidget extends Component {
  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetTopUserStats(1, 5);
    this.setState({ response });
  }

  render() {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    return (
      <Card className="top-scorers-widget" title="Top scorers">
        {this.renderTable()}
      </Card>
    );
  }

  renderTable = () => {
    const { data, profilesMap } = this.state.response;
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
}

const mapDispatchToProps = {
  onGetTopUserStats: widgetActions.getTopUserStats,
};
export default connect(undefined, mapDispatchToProps)(TopScorersWidget);
