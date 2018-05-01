import * as React from 'react';
import { FormattedRelative } from 'react-intl';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import { UsersMap } from '../../../../../../../../../../modules/api/jophiel/user';
import { Contest, ContestStyle } from '../../../../../../../../../../modules/api/uriel/contest';
import {
  ContestScoreboard,
  ContestScoreboardResponse,
} from '../../../../../../../../../../modules/api/uriel/contestScoreboard';
import { IcpcScoreboard, IoiScoreboard } from '../../../../../../../../../../modules/api/uriel/scoreboard';
import { AppState } from '../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../modules/contestSelectors';
import { IcpcScoreboardTable } from '../IcpcScoreboardTable/IcpcScoreboardTable';
import { IoiScoreboardTable } from '../IoiScoreboardTable/IoiScoreboardTable';
import { contestScoreboardActions as injectedContestScoreboardActions } from '../modules/contestScoreboardActions';

import './ContestScoreboardPage.css';

interface ContestScoreboardPageProps {
  contest: Contest;
  onFetchScoreboard: (contestJid: string) => Promise<ContestScoreboardResponse | null>;
}

interface ContestScoreboardPageState {
  scoreboard?: ContestScoreboard[];
  usersMap?: UsersMap;
}

class ContestScoreboardPage extends React.Component<ContestScoreboardPageProps, ContestScoreboardPageState> {
  state: ContestScoreboardPageState = {};

  async componentDidMount() {
    const response = await this.props.onFetchScoreboard(this.props.contest.jid);
    if (!response) {
      this.setState({ scoreboard: [] });
    } else {
      const { data, usersMap } = response;
      this.setState({
        scoreboard: [data],
        usersMap,
      });
    }
  }

  render() {
    return (
      <ContentCard className="contest-scoreboard-page">
        <h3>Scoreboard</h3>
        {this.renderScoreboardUpdatedTime()}
        <div className="clearfix" />
        <hr />
        {this.renderScoreboard()}
      </ContentCard>
    );
  }

  private renderScoreboardUpdatedTime = () => {
    const { scoreboard } = this.state;
    if (!scoreboard || scoreboard.length === 0) {
      return null;
    }

    return (
      <p className="contest-scoreboard-page__info">
        <small>
          last updated <FormattedRelative value={scoreboard[0].updatedTime} />
        </small>
      </p>
    );
  };

  private renderScoreboard = () => {
    const { scoreboard, usersMap } = this.state;
    if (!scoreboard) {
      return <LoadingState />;
    }

    if (scoreboard.length === 0) {
      return (
        <p>
          <small>
            <em>No scoreboard.</em>
          </small>
        </p>
      );
    }

    if (this.props.contest.style === ContestStyle.ICPC) {
      return <IcpcScoreboardTable scoreboard={scoreboard[0].scoreboard as IcpcScoreboard} usersMap={usersMap!} />;
    } else {
      return <IoiScoreboardTable scoreboard={scoreboard[0].scoreboard as IoiScoreboard} usersMap={usersMap!} />;
    }
  };
}

function createContestScoreboardPage(contestScoreboardActions) {
  const mapStateToProps = (state: AppState) =>
    ({
      contest: selectContest(state)!,
    } as Partial<ContestScoreboardPageProps>);

  const mapDispatchToProps = {
    onFetchScoreboard: contestScoreboardActions.fetch,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestScoreboardPage));
}

export default createContestScoreboardPage(injectedContestScoreboardActions);
