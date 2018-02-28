import { Card } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { Contest, ContestStyle } from '../../../../../../../../../../modules/api/uriel/contest';
import { IcpcScoreboard, Scoreboard } from '../../../../../../../../../../modules/api/uriel/scoreboard';
import { AppState } from '../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../../../modules/contestSelectors';
import { contestActions as injectedContestActions } from '../../../../../../../modules/contestActions';
import { IcpcScoreboardTable } from '../IcpcScoreboardTable/IcpcScoreboardTable';

interface ContestScoreboardPageProps extends RouteComponentProps<{ contestJid: string }> {
  contest?: Contest;
  onGetScoreboard: (contestJid: string) => Promise<Scoreboard>;
}

interface ContestScoreboardPageState {
  scoreboard?: Scoreboard;
}

class ContestScoreboardPage extends React.Component<ContestScoreboardPageProps, ContestScoreboardPageState> {
  state: ContestScoreboardPageState = {};

  async componentWillReceiveProps(nextProps: ContestScoreboardPageProps) {
    if (nextProps.contest) {
      const scoreboard = await this.props.onGetScoreboard(nextProps.contest.jid);
      this.setState({ scoreboard });
    }
  }

  render() {
    if (!this.props.contest || !this.state.scoreboard) {
      return null;
    }

    const { contest } = this.props;
    const { scoreboard } = this.state;

    return <Card>{this.renderScoreboard(contest.style, scoreboard)}</Card>;
  }

  private renderScoreboard = (style: ContestStyle, scoreboard: Scoreboard) => {
    if (style === ContestStyle.ICPC) {
      return <IcpcScoreboardTable scoreboard={scoreboard as IcpcScoreboard} />;
    }
    return null;
  };
}

function createContestScoreboardPage(contestActions) {
  const mapStateToProps = (state: AppState) =>
    ({
      contest: selectContest(state),
    } as Partial<ContestScoreboardPageProps>);

  const mapDispatchToProps = {
    onGetScoreboard: contestActions.getScoreboard,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestScoreboardPage));
}

export default createContestScoreboardPage(injectedContestActions);
