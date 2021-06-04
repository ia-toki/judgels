import { Button, Callout, Intent, Switch } from '@blueprintjs/core';
import { parse, stringify } from 'query-string';
import { Component, Fragment } from 'react';
import { connect } from 'react-redux';
import { push } from 'connected-react-router';

import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { FormattedRelative } from '../../../../../../components/FormattedRelative/FormattedRelative';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { SubmissionImageDialog } from '../../../../../../components/SubmissionImageDialog/SubmissionImageDialog';
import { ContestStyle } from '../../../../../../modules/api/uriel/contest';
import { ContestScoreboardType } from '../../../../../../modules/api/uriel/contestScoreboard';
import { selectMaybeUserJid } from '../../../../../../modules/session/sessionSelectors';
import { selectContest } from '../../../modules/contestSelectors';
import { TrocScoreboardTable } from '../TrocScoreboardTable/TrocScoreboardTable';
import { IcpcScoreboardTable } from '../IcpcScoreboardTable/IcpcScoreboardTable';
import { IoiScoreboardTable } from '../IoiScoreboardTable/IoiScoreboardTable';
import { GcjScoreboardTable } from '../GcjScoreboardTable/GcjScoreboardTable';
import { BundleScoreboardTable } from '../BundleScoreboardTable/BundleScoreboardPage';
import * as contestScoreboardActions from '../modules/contestScoreboardActions';

import './ContestScoreboardPage.scss';

export class ContestScoreboardPage extends Component {
  static PAGE_SIZE = 250;
  state;

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const frozen = !!queries.frozen;
    const showClosedProblems = !!queries.showClosedProblems;
    this.state = {
      response: undefined,
      frozen,
      showClosedProblems,
      lastRefreshScoreboardTime: 0,
      isForceRefreshButtonLoading: false,
      isSubmissionImageDialogOpen: false,
      submissionImageUrl: undefined,
      submissionDialogTitle: '',
    };
  }

  render() {
    const { lastRefreshScoreboardTime } = this.state;

    return (
      <ContentCard className="contest-scoreboard-page">
        <h3>Scoreboard</h3>
        {this.renderScoreboardUpdatedTime()}
        <div className="clearfix" />
        <hr />
        {this.renderFilter()}
        {this.renderForceRefreshButton()}
        <div className="clearfix" />
        {this.renderFrozenScoreboardNotice()}
        {this.renderScoreboard()}
        <Pagination
          key={lastRefreshScoreboardTime}
          currentPage={1}
          pageSize={ContestScoreboardPage.PAGE_SIZE}
          onChangePage={this.onChangePage}
        />
        <SubmissionImageDialog
          isOpen={this.state.isSubmissionImageDialogOpen}
          onClose={this.toggleSubmissionImageDialog}
          title={this.state.submissionDialogTitle}
          imageUrl={this.state.submissionImageUrl}
        />
      </ContentCard>
    );
  }

  toggleSubmissionImageDialog = () => {
    this.setState({ isSubmissionImageDialogOpen: !this.state.isSubmissionImageDialogOpen });
  };

  onChangePage = async nextPage => {
    const scoreboard = await this.refreshScoreboard(nextPage, this.state.frozen, this.state.showClosedProblems);
    if (scoreboard) {
      return scoreboard.data.totalEntries;
    } else {
      return 0;
    }
  };

  refreshScoreboard = async (nextPage, frozen, showClosedProblems) => {
    const response = await this.props.onGetScoreboard(this.props.contest.jid, frozen, showClosedProblems, nextPage);
    this.setState({ response: response ? [response] : [], frozen, showClosedProblems });
    return response;
  };

  renderScoreboardUpdatedTime = () => {
    const { response } = this.state;
    if (!response || response.length === 0) {
      return null;
    }

    const scoreboard = response[0].data;
    const contestEndTime = this.props.contest.beginTime + this.props.contest.duration;
    return (
      <p className="contest-scoreboard-page__info">
        <small>
          last updated <FormattedRelative value={Math.min(contestEndTime, scoreboard.updatedTime)} />
        </small>
      </p>
    );
  };

  renderFrozenScoreboardNotice = () => {
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

  renderFilter = () => {
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
            onChange={this.onChangeShowClosedProblems}
          />
        )}
        <div className="clearfix " />
      </div>
    );
  };

  renderForceRefreshButton = () => {
    const { response } = this.state;
    if (!response || response.length === 0) {
      return null;
    }

    const { canRefresh } = response[0].config;
    if (!canRefresh) {
      return null;
    }

    return (
      <Button
        className="contest-scoreboard-page__refresh-button"
        intent="primary"
        icon="refresh"
        onClick={this.forceRefreshScoreboard}
        loading={!!this.state.isForceRefreshButtonLoading}
      >
        Force refresh
      </Button>
    );
  };

  forceRefreshScoreboard = async () => {
    this.setState({ isForceRefreshButtonLoading: true });
    await this.props.onRefreshScoreboard(this.props.contest.jid);
    this.setState({ isForceRefreshButtonLoading: false });
  };

  onChangeFrozen = ({ target }) => this.onChange(target.checked, this.state.showClosedProblems);

  onChangeShowClosedProblems = ({ target }) => this.onChange(this.state.frozen, target.checked);

  onChange = (frozen, showClosedProblems) => {
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

  onOpenSubmissionImage = async (contestJid, contestantJid, problemJid) => {
    const [info, submissionImageUrl] = await Promise.all([
      this.props.onGetSubmissionInfo(contestJid, contestantJid, problemJid),
      this.props.onGetSubmissionSourceImage(contestJid, contestantJid, problemJid),
    ]);
    const submissionDialogTitle = `Submission #${info.id} (${info.profile.username})`;
    this.setState({ submissionImageUrl, submissionDialogTitle });
    this.toggleSubmissionImageDialog();
  };

  renderScoreboard = () => {
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

    const {
      data: scoreboard,
      profilesMap,
      config: { canViewSubmissions },
    } = response[0];
    if (this.props.contest.style === ContestStyle.TROC) {
      return (
        <TrocScoreboardTable
          userJid={this.props.userJid}
          contestJid={this.props.contest.jid}
          onOpenSubmissionImage={this.onOpenSubmissionImage}
          scoreboard={scoreboard.scoreboard}
          profilesMap={profilesMap}
          canViewSubmissions={canViewSubmissions}
        />
      );
    } else if (this.props.contest.style === ContestStyle.ICPC) {
      return (
        <IcpcScoreboardTable
          userJid={this.props.userJid}
          contestJid={this.props.contest.jid}
          onOpenSubmissionImage={this.onOpenSubmissionImage}
          scoreboard={scoreboard.scoreboard}
          profilesMap={profilesMap}
          canViewSubmissions={canViewSubmissions}
        />
      );
    } else if (this.props.contest.style === ContestStyle.IOI) {
      return (
        <IoiScoreboardTable
          userJid={this.props.userJid}
          contestJid={this.props.contest.jid}
          onOpenSubmissionImage={this.onOpenSubmissionImage}
          scoreboard={scoreboard.scoreboard}
          profilesMap={profilesMap}
          canViewSubmissions={canViewSubmissions}
        />
      );
    } else if (this.props.contest.style === ContestStyle.Bundle) {
      return (
        <BundleScoreboardTable
          userJid={this.props.userJid}
          scoreboard={scoreboard.scoreboard}
          profilesMap={profilesMap}
        />
      );
    } else if (this.props.contest.style === ContestStyle.GCJ) {
      return (
        <GcjScoreboardTable
          userJid={this.props.userJid}
          contestJid={this.props.contest.jid}
          onOpenSubmissionImage={this.onOpenSubmissionImage}
          scoreboard={scoreboard.scoreboard}
          profilesMap={profilesMap}
          canViewSubmissions={canViewSubmissions}
        />
      );
    } else {
      return <Fragment />;
    }
  };
}

const mapStateToProps = state => ({
  userJid: selectMaybeUserJid(state),
  contest: selectContest(state),
});

const mapDispatchToProps = {
  onGetScoreboard: contestScoreboardActions.getScoreboard,
  onRefreshScoreboard: contestScoreboardActions.refreshScoreboard,
  onGetSubmissionSourceImage: contestScoreboardActions.getSubmissionSourceImage,
  onGetSubmissionInfo: contestScoreboardActions.getSubmissionInfo,
  onAppendRoute: queries => push({ search: stringify(queries) }),
};

export default withBreadcrumb('Scoreboard')(connect(mapStateToProps, mapDispatchToProps)(ContestScoreboardPage));
