import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';
import { withRouter } from 'react-router';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import { ProgressBar } from '../../../../../../../components/ProgressBar/ProgressBar';
import { VerdictProgressTag } from '../../../../../../../components/VerdictProgressTag/VerdictProgressTag';
import { selectProblemSet } from '../../../../modules/problemSetSelectors';
import * as problemSetProblemActions from '../../modules/problemSetProblemActions';

import './ProblemStatsWidget.css';

class ProblemStatsWidget extends React.Component {
  static TOP_STATS_SIZE = 5;

  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetProblemStats(
      this.props.problemSet.jid,
      this.props.match.params.problemAlias
    );
    this.setState({ response });
  }

  render() {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    const { progress, stats, topStats, profilesMap } = response;
    return (
      <>
        {this.renderStats(progress, stats)}
        {this.renderTopStats(topStats, profilesMap)}
      </>
    );
  }

  renderStats = (progress, stats) => {
    return (
      <ContentCard className="problem-stats-widget">
        <h4>Stats</h4>
        <hr />
        <ul>
          {stats.totalUsersAccepted > 0 && (
            <li>
              Users solved: <b className="stats-value">{stats.totalUsersAccepted}</b>
            </li>
          )}
          <li>
            Avg score:{' '}
            <span className="stats-value">
              <b>{Math.ceil(stats.totalScores / (stats.totalUsersTried || 1))}</b> / {stats.totalUsersTried} users
            </span>
          </li>
          <li>
            Your score: <VerdictProgressTag {...progress} />
            <br />
            <ProgressBar num={progress.score} denom={100} verdict={progress.verdict} />
          </li>
        </ul>
      </ContentCard>
    );
  };

  renderTopStats = (topStats, profilesMap) => {
    const { topUsersByScore, topUsersByTime, topUsersByMemory } = topStats;
    if (
      topUsersByScore.length === ProblemStatsWidget.TOP_STATS_SIZE &&
      topUsersByScore[ProblemStatsWidget.TOP_STATS_SIZE - 1].stats >= 100
    ) {
      return (
        <>
          {this.renderTopTime(topUsersByTime, profilesMap)}
          {this.renderTopMemory(topUsersByMemory, profilesMap)}
        </>
      );
    } else {
      return this.renderTopScore(topUsersByScore, profilesMap);
    }
  };

  renderTopScore = (entries, profilesMap) => {
    return this.renderTopEntries(entries, profilesMap, 'score', 'Score', '');
  };

  renderTopTime = (entries, profilesMap) => {
    return this.renderTopEntries(entries, profilesMap, 'time', 'Time', 'ms');
  };
  renderTopMemory = (entries, profilesMap) => {
    return this.renderTopEntries(entries, profilesMap, 'memory', 'Memory', 'KB');
  };

  renderTopEntries = (entries, profilesMap, title, header, suffix) => {
    if (entries.length === 0) {
      return null;
    }

    const rows = entries.map((e, idx) => (
      <tr key={e.userJid}>
        <td>{idx + 1}</td>
        <td>
          <UserRef profile={profilesMap[e.userJid]} />
        </td>
        <td>
          {e.stats} {suffix}
        </td>
      </tr>
    ));

    return (
      <ContentCard className="problem-stats-widget">
        <h4>Top users by {title}</h4>
        <HTMLTable striped className="table-list stats-table">
          <thead>
            <tr>
              <th className="col-rank">#</th>
              <th>User</th>
              <th className="col-value">{header}</th>
            </tr>
          </thead>
          <tbody>{rows}</tbody>
        </HTMLTable>
      </ContentCard>
    );
  };
}

const mapStateToProps = state => ({
  problemSet: selectProblemSet(state),
});
const mapDispatchToProps = {
  onGetProblemStats: problemSetProblemActions.getProblemStats,
};
export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ProblemStatsWidget));
