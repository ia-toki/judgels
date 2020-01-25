import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { AppState } from '../../../../../../modules/store';
import { Contest } from '../../../../../../modules/api/uriel/contest';
import {
  ContestContestantsDeleteResponse,
  ContestContestantsResponse,
  ContestContestantUpsertResponse,
} from '../../../../../../modules/api/uriel/contestContestant';
import { ContestContestantsTable } from '../ContestContestantsTable/ContestContestantsTable';
import { ContestContestantAddDialog } from '../ContestContestantAddDialog/ContestContestantAddDialog';
import { ContestContestantRemoveDialog } from '../ContestContestantRemoveDialog/ContestContestantRemoveDialog';
import { selectContest } from '../../../modules/contestSelectors';
import { contestContestantActions as injectedContestContestantActions } from '../../modules/contestContestantActions';
import { contestActions as injectedContestActions } from '../../../modules/contestActions';

import './ContestContestantsPage.css';

export interface ContestContestantsPageProps {
  contest: Contest;
  onGetContestants: (contestJid: string, page?: number) => Promise<ContestContestantsResponse>;
  onUpsertContestants: (contestJid: string, usernames: string[]) => Promise<ContestContestantUpsertResponse>;
  onDeleteContestants: (contestJid: string, usernames: string[]) => Promise<ContestContestantsDeleteResponse>;
  onResetVirtualContest: (contestJid: string) => Promise<void>;
}

interface ContestContestantsPageState {
  response?: ContestContestantsResponse;
  lastRefreshContestantsTime?: number;
}

class ContestContestantsPage extends React.Component<ContestContestantsPageProps, ContestContestantsPageState> {
  private static PAGE_SIZE = 250;

  state: ContestContestantsPageState = {};

  render() {
    return (
      <ContentCard>
        <h3>Contestants</h3>
        <hr />
        {this.renderAddRemoveDialogs()}
        {this.renderContestants()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private renderContestants = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: contestants, profilesMap } = response;
    if (contestants.totalCount === 0) {
      return (
        <p>
          <small>No contestants.</small>
        </p>
      );
    }

    return <ContestContestantsTable contestants={contestants.page} profilesMap={profilesMap} />;
  };

  private renderPagination = () => {
    // updates pagination when contestants are refreshed
    const { lastRefreshContestantsTime } = this.state;
    const key = lastRefreshContestantsTime || 0;

    return (
      <Pagination
        key={key}
        currentPage={1}
        pageSize={ContestContestantsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  private onChangePage = async (nextPage: number) => {
    const data = await this.refreshContestants(nextPage);
    return data.totalCount;
  };

  private refreshContestants = async (page?: number) => {
    const response = await this.props.onGetContestants(this.props.contest.jid, page);
    this.setState({ response });
    return response.data;
  };

  private renderAddRemoveDialogs = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    if (!response.config.canManage) {
      return null;
    }
    const { data: contestants } = response;
    const isVirtualContest = contestants.page.some(contestant => contestant.contestStartTime !== null);
    return (
      <>
        <ContestContestantAddDialog contest={this.props.contest} onUpsertContestants={this.upsertContestants} />
        <ContestContestantRemoveDialog contest={this.props.contest} onDeleteContestants={this.deleteContestants} />
        {isVirtualContest && (
          <Button
            className="contest-contestant-dialog-button"
            intent={Intent.DANGER}
            icon="refresh"
            onClick={this.onResetVirtualContest}
          >
            Reset all contestant virtual start times
          </Button>
        )}
        <div className="clearfix" />
      </>
    );
  };

  private onResetVirtualContest = async () => {
    if (window.confirm('Are you sure to reset all contestant virtual start times?')) {
      await this.props.onResetVirtualContest(this.props.contest.jid);
      this.setState({ lastRefreshContestantsTime: new Date().getTime() });
    }
  };

  private upsertContestants = async (contestJid, data) => {
    const response = await this.props.onUpsertContestants(contestJid, data);
    this.setState({ lastRefreshContestantsTime: new Date().getTime() });
    return response;
  };

  private deleteContestants = async (contestJid, data) => {
    const response = await this.props.onDeleteContestants(contestJid, data);
    this.setState({ lastRefreshContestantsTime: new Date().getTime() });
    return response;
  };
}

export function createContestContestantsPage(contestContestantActions, contestActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetContestants: contestContestantActions.getContestants,
    onUpsertContestants: contestContestantActions.upsertContestants,
    onDeleteContestants: contestContestantActions.deleteContestants,
    onResetVirtualContest: contestActions.resetVirtualContest,
  };

  return withBreadcrumb('Contestants')(connect(mapStateToProps, mapDispatchToProps)(ContestContestantsPage));
}

export default createContestContestantsPage(injectedContestContestantActions, injectedContestActions);
