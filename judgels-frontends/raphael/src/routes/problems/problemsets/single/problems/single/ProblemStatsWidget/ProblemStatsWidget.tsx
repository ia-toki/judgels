import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import { ProgressBar } from '../../../../../../../components/ProgressBar/ProgressBar';
import { VerdictProgressTag } from '../../../../../../../components/VerdictProgressTag/VerdictProgressTag';
import { ProblemSet } from '../../../../../../../modules/api/jerahmeel/problemSet';
import {
  ProblemProgress,
  ProblemStats,
  ProblemTopStats,
  ProblemTopStatsEntry,
} from '../../../../../../../modules/api/jerahmeel/problem';
import { ProblemStatsResponse } from '../../../../../../../modules/api/jerahmeel/problemSetProblem';
import { selectProblemSet } from '../../../../modules/problemSetSelectors';
import { problemSetProblemActions as injectedProblemSetProblemActions } from '../../modules/problemSetProblemActions';

import './ProblemStatsWidget.css';

export interface ProblemStatsWidgetProps extends RouteComponentProps<{ problemAlias: string }> {
  problemSet: ProblemSet;
  onGetProblemStats: (problemSetJid: string, problemAlias: string) => Promise<ProblemStatsResponse>;
}

interface ProblemStatsWidgetState {
  response?: ProblemStatsResponse;
}

class ProblemStatsWidget extends React.Component<ProblemStatsWidgetProps, ProblemStatsWidgetState> {
  private static TOP_STATS_SIZE = 5;

  state: ProblemStatsWidgetState = {};

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

  private renderStats = (progress: ProblemProgress, stats: ProblemStats) => {
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

  private renderTopStats = (topStats: ProblemTopStats, profilesMap) => {
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

  private renderTopScore = (entries: ProblemTopStatsEntry[], profilesMap) => {
    return this.renderTopEntries(entries, profilesMap, 'score', 'Score', '');
  };

  private renderTopTime = (entries: ProblemTopStatsEntry[], profilesMap) => {
    return this.renderTopEntries(entries, profilesMap, 'time', 'Time', 'ms');
  };
  private renderTopMemory = (entries: ProblemTopStatsEntry[], profilesMap) => {
    return this.renderTopEntries(entries, profilesMap, 'memory', 'Memory', 'KB');
  };

  private renderTopEntries = (
    entries: ProblemTopStatsEntry[],
    profilesMap,
    title: string,
    header: string,
    suffix: string
  ) => {
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

export function createProblemStatsWidget(problemSetProblemActions) {
  const mapStateToProps = state => ({
    problemSet: selectProblemSet(state),
  });
  const mapDispatchToProps = {
    onGetProblemStats: problemSetProblemActions.getProblemStats,
  };
  return withRouter(connect(mapStateToProps, mapDispatchToProps)(ProblemStatsWidget));
}

export default createProblemStatsWidget(injectedProblemSetProblemActions);
