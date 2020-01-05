import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { ProblemSet } from '../../../../../../../modules/api/jerahmeel/problemSet';
import { ProblemProgress, ProblemStats } from '../../../../../../../modules/api/jerahmeel/problem';
import { ProblemStatsResponse } from '../../../../../../../modules/api/jerahmeel/problemSetProblem';
import { selectProblemSet } from '../../../../modules/problemSetSelectors';
import { problemSetProblemActions as injectedProblemSetProblemActions } from '../../modules/problemSetProblemActions';

import './ProblemStatsWidget.css';
import { VerdictProgressTag } from '../../../../../../../components/VerdictProgressTag/VerdictProgressTag';

export interface ProblemStatsWidgetProps extends RouteComponentProps<{ problemAlias: string }> {
  problemSet: ProblemSet;
  onGetProblemStats: (problemSetJid: string, problemAlias: string) => Promise<ProblemStatsResponse>;
}

interface ProblemStatsWidgetState {
  response?: ProblemStatsResponse;
}

class ProblemStatsWidget extends React.Component<ProblemStatsWidgetProps, ProblemStatsWidgetState> {
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
    const { progress, stats } = response;
    return <>{this.renderStats(progress, stats)}</>;
  }

  private renderStats = (progress: ProblemProgress, stats: ProblemStats) => {
    return (
      <ContentCard className="problem-stats-widget">
        <h4>Stats</h4>
        <hr />
        <ul>
          {stats.totalUsersAccepted > 0 && (
            <li>
              Users accepted: <b className="stats-value">{stats.totalUsersAccepted}</b>
            </li>
          )}
          <li>
            Users attempted: <b className="stats-value">{stats.totalUsersTried}</b>
          </li>
          <li>
            Avg score:
            <b className="stats-value">{Math.ceil(stats.totalScores / (stats.totalUsersAccepted || 1))} pts</b>
          </li>
          <li>
            Your score: <VerdictProgressTag {...progress} />
          </li>
        </ul>
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
