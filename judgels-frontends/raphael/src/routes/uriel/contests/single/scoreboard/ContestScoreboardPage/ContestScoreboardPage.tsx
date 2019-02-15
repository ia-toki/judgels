import { Callout, Intent, Switch } from '@blueprintjs/core';
import { parse, stringify } from 'query-string';
import * as React from 'react';
import { FormattedRelative } from 'react-intl';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';
import { push } from 'react-router-redux';

import { LoadingState } from 'components/LoadingState/LoadingState';
import Pagination from 'components/Pagination/Pagination';
import { ContentCard } from 'components/ContentCard/ContentCard';
import { Contest, ContestStyle } from 'modules/api/uriel/contest';
import { ContestScoreboardResponse, ContestScoreboardType } from 'modules/api/uriel/contestScoreboard';
import { IcpcScoreboard, IoiScoreboard, GcjScoreboard } from 'modules/api/uriel/scoreboard';
import { AppState } from 'modules/store';
import { selectMaybeUserJid } from 'modules/session/sessionSelectors';

import { selectContest } from '../../../modules/contestSelectors';
import { IcpcScoreboardTable } from '../IcpcScoreboardTable/IcpcScoreboardTable';
import { IoiScoreboardTable } from '../IoiScoreboardTable/IoiScoreboardTable';
import { GcjScoreboardTable } from '../GcjScoreboardTable/GcjScoreboardTable';
import { contestScoreboardActions as injectedContestScoreboardActions } from '../modules/contestScoreboardActions';

import './ContestScoreboardPage.css';

export interface ContestScoreboardPageProps
  extends RouteComponentProps<{ frozen: boolean; showClosedProblems: boolean }> {
  userJid?: string;
  contest: Contest;
  onGetScoreboard: (
    contestJid: string,
    frozen?: boolean,
    showClosedProblems?: boolean,
    page?: number
  ) => Promise<ContestScoreboardResponse | null>;
  onAppendRoute: (queries: any) => any;
}

interface ContestScoreboardPageState {
  response?: ContestScoreboardResponse[];
  frozen?: boolean;
  showClosedProblems?: boolean;
  lastRefreshScoreboardTime?: number;
}

export class ContestScoreboardPage extends React.PureComponent<ContestScoreboardPageProps, ContestScoreboardPageState> {
  private static PAGE_SIZE = 50;
  state: ContestScoreboardPageState = {};

  async componentDidMount() {
    const queries = parse(this.props.location.search);
    const frozen = !!queries.frozen;
    const showClosedProblems = !!queries.showClosedProblems;
    await this.refreshScoreboard(1, frozen, showClosedProblems);
  }

  render() {
    const { lastRefreshScoreboardTime } = this.state;
    const key = lastRefreshScoreboardTime || 0;

    return (
      <ContentCard className="contest-scoreboard-page">
        <h3>Scoreboard</h3>
        {this.renderScoreboardUpdatedTime()}
        <div className="clearfix" />
        <hr />
        {this.renderFilter()}
        {this.renderFrozenScoreboardNotice()}
        {this.renderScoreboard()}
        <Pagination
          key={key}
          currentPage={1}
          pageSize={ContestScoreboardPage.PAGE_SIZE}
          onChangePage={this.onChangePage}
        />
      </ContentCard>
    );
  }

  private onChangePage = async (nextPage: number) => {
    const scoreboard = await this.refreshScoreboard(nextPage, this.state.frozen, this.state.showClosedProblems);
    if (scoreboard) {
      return scoreboard.data.totalEntries;
    } else {
      return 0;
    }
  };

  private refreshScoreboard = async (nextPage?: number, frozen?: boolean, showClosedProblems?: boolean) => {
    const response = await this.props.onGetScoreboard(
      this.props.contest.jid,
      frozen,
      showClosedProblems,
      nextPage
    );
    this.setState({ response: response ? [response] : [], frozen, showClosedProblems });
    return response;
  };

  private renderScoreboardUpdatedTime = () => {
    const { response } = this.state;
    if (!response || response.length === 0) {
      return null;
    }

    const scoreboard = response[0].data;
    return (
      <p className="contest-scoreboard-page__info">
        <small>
          last updated <FormattedRelative value={scoreboard.updatedTime} />
        </small>
      </p>
    );
  };

  private renderFrozenScoreboardNotice = () => {
    const { response } = this.state;
    if (!response || response.length === 0 || response[0].data.type !== ContestScoreboardType.Frozen) {
      return null;
    }

    return (
      <Callout
        className="contest-scoreboard-page__frozen"
        icon="pause"
        intent={Intent.WARNING}
        title="SCOREBOARD HAS BEEN FROZEN"
      />
    );
  };

  private renderFilter = () => {
    const { response } = this.state;
    if (!response || response.length === 0) {
      return null;
    }

    const { canViewOfficialAndFrozen, canViewClosedProblems } = response[0].config;
    if (!canViewOfficialAndFrozen && !canViewClosedProblems) {
      return null;
    }

    return (
      <div className="contest-scoreboard-page__filters">
        {canViewOfficialAndFrozen && (
          <Switch
            className="contest-scoreboard-page__filter"
            label="Frozen"
            checked={this.state.frozen}
            onChange={this.onChangeFrozen}
          />
        )}
        {canViewClosedProblems && (
          <Switch
            className="contest-scoreboard-page__filter"
            label="Show closed problems"
            checked={this.state.showClosedProblems}
            onChange={this.onChangeshowClosedProblems}
          />
        )}
        <div className="clearfix " />
      </div>
    );
  };

  private onChangeFrozen = ({ target }) => this.onChange(target.checked, this.state.showClosedProblems);

  private onChangeshowClosedProblems = ({ target }) => this.onChange(this.state.frozen, target.checked);

  private onChange = (frozen?: boolean, showClosedProblems?: boolean) => {
    this.setState({ frozen, showClosedProblems });
    this.refreshScoreboard(1, frozen, showClosedProblems);
    this.setState({ lastRefreshScoreboardTime: new Date().getTime() });

    let queries = parse(this.props.location.search);
    queries = { ...queries, frozen: undefined, showClosedProblems: undefined };
    if (frozen) {
      queries = { ...queries, frozen };
    }
    if (showClosedProblems) {
      queries = { ...queries, showClosedProblems };
    }
    this.props.onAppendRoute(queries);
  };

  private renderScoreboard = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }
    if (response.length === 0) {
      return (
        <p>
          <small>No scoreboard.</small>
        </p>
      );
    }

    const { data: scoreboard, profilesMap } = response[0];
    if (this.props.contest.style === ContestStyle.ICPC) {
      return (
        <IcpcScoreboardTable
          userJid={this.props.userJid}
          scoreboard={scoreboard.scoreboard as IcpcScoreboard}
          profilesMap={profilesMap!}
        />
      );
    } else if (this.props.contest.style === ContestStyle.IOI) {
      return (
        <IoiScoreboardTable
          userJid={this.props.userJid}
          scoreboard={scoreboard.scoreboard as IoiScoreboard}
          profilesMap={profilesMap!}
        />
      );
    } else {
      return (
        <GcjScoreboardTable
          userJid={this.props.userJid}
          scoreboard={scoreboard.scoreboard as GcjScoreboard}
          profilesMap={profilesMap!}
        />
      );
    }
  };
}

export function createContestScoreboardPage(contestScoreboardActions) {
  const mapStateToProps = (state: AppState) =>
    ({
      userJid: selectMaybeUserJid(state),
      contest: selectContest(state)!,
    } as Partial<ContestScoreboardPageProps>);

  const mapDispatchToProps = {
    onGetScoreboard: contestScoreboardActions.getScoreboard,
    onAppendRoute: (queries: any) => push({ search: stringify(queries) }),
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestScoreboardPage));
}

export default createContestScoreboardPage(injectedContestScoreboardActions);
