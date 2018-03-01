import { Card } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { Contest, ContestStyle } from '../../../../../../../../../../modules/api/uriel/contest';
import { ContestScoreboard } from '../../../../../../../../../../modules/api/uriel/contestScoreboard';
import { IcpcScoreboard, Scoreboard } from '../../../../../../../../../../modules/api/uriel/scoreboard';
import { AppState } from '../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../modules/contestSelectors';
import { IcpcScoreboardTable } from '../IcpcScoreboardTable/IcpcScoreboardTable';
import { contestScoreboardActions as injectedContestScoreboardActions } from '../modules/contestScoreboardActions';

interface ContestScoreboardPageProps extends RouteComponentProps<{ contestJid: string }> {
  contest?: Contest;
  onGetScoreboard: (contestJid: string) => Promise<ContestScoreboard>;
}

interface ContestScoreboardPageState {
  contestScoreboard?: ContestScoreboard;
}

class ContestScoreboardPage extends React.Component<ContestScoreboardPageProps, ContestScoreboardPageState> {
  state: ContestScoreboardPageState = {};

  async componentWillReceiveProps(nextProps: ContestScoreboardPageProps) {
    if (nextProps.contest) {
      const contestScoreboard = await this.props.onGetScoreboard(nextProps.contest.jid);
      this.setState({ contestScoreboard });
    }
  }

  render() {
    if (!this.props.contest || !this.state.contestScoreboard) {
      return null;
    }

    const { contest } = this.props;
    const { contestScoreboard } = this.state;

    return <Card>{this.renderScoreboard(contest.style, contestScoreboard.scoreboard)}</Card>;
  }

  private renderScoreboard = (style: ContestStyle, scoreboard: Scoreboard) => {
    if (style === ContestStyle.ICPC) {
      return <IcpcScoreboardTable scoreboard={scoreboard as IcpcScoreboard} />;
    }
    return null;
  };
}

function createContestScoreboardPage(contestScoreboardActions) {
  const mapStateToProps = (state: AppState) =>
    ({
      contest: selectContest(state),
    } as Partial<ContestScoreboardPageProps>);

  const mapDispatchToProps = {
    onGetScoreboard: contestScoreboardActions.get,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestScoreboardPage));
}

export default createContestScoreboardPage(injectedContestScoreboardActions);
