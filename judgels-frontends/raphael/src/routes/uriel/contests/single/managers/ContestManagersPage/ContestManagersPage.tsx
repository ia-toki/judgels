import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from 'components/ContentCard/ContentCard';
import { LoadingState } from 'components/LoadingState/LoadingState';
import Pagination from 'components/Pagination/Pagination';
import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';
import {
  ContestManagerDeleteResponse,
  ContestManagersResponse,
  ContestManagerUpsertResponse,
} from 'modules/api/uriel/contestManager';

import { ContestManagersTable } from '../ContestManagersTable/ContestManagersTable';
import { ContestManagerAddDialog } from '../ContestManagerAddDialog/ContestManagerAddDialog';
import { ContestManagerRemoveDialog } from '../ContestManagerRemoveDialog/ContestManagerRemoveDialog';
import { selectContest } from '../../../modules/contestSelectors';
import { contestManagerActions as injectedContestManagerActions } from '../modules/contestManagerActions';

import './ContestManagersPage.css';

export interface ContestManagersPageProps {
  contest: Contest;
  onGetManagers: (contestJid: string, page?: number) => Promise<ContestManagersResponse>;
  onUpsertManagers: (contestJid: string, usernames: string[]) => Promise<ContestManagerUpsertResponse>;
  onDeleteManagers: (contestJid: string, usernames: string[]) => Promise<ContestManagerDeleteResponse>;
}

interface ContestManagersPageState {
  response?: ContestManagersResponse;
  lastRefreshManagersTime?: number;
}

class ContestManagersPage extends React.Component<ContestManagersPageProps, ContestManagersPageState> {
  private static PAGE_SIZE = 100;

  state: ContestManagersPageState = {};

  render() {
    return (
      <ContentCard>
        <h3>Managers</h3>
        <hr />
        {this.renderAddRemoveDialogs()}
        {this.renderManagers()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private renderManagers = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: managers, profilesMap } = response;
    if (managers.totalCount === 0) {
      return (
        <p>
          <small>No managers.</small>
        </p>
      );
    }

    return <ContestManagersTable managers={managers.page} profilesMap={profilesMap} />;
  };

  private renderPagination = () => {
    // updates pagination when managers are refreshed
    const { lastRefreshManagersTime } = this.state;
    const key = lastRefreshManagersTime || 0;

    return (
      <Pagination key={key} currentPage={1} pageSize={ContestManagersPage.PAGE_SIZE} onChangePage={this.onChangePage} />
    );
  };

  private onChangePage = async (nextPage: number) => {
    const data = await this.refreshManagers(nextPage);
    return data.totalCount;
  };

  private refreshManagers = async (page?: number) => {
    const response = await this.props.onGetManagers(this.props.contest.jid, page);
    this.setState({ response });
    return response.data;
  };

  private renderAddRemoveDialogs = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    return (
      <>
        <ContestManagerAddDialog contest={this.props.contest} onUpsertManagers={this.upsertManagers} />
        <ContestManagerRemoveDialog contest={this.props.contest} onDeleteManagers={this.deleteManagers} />
        <div className="clearfix" />
      </>
    );
  };

  private upsertManagers = async (contestJid, data) => {
    const response = await this.props.onUpsertManagers(contestJid, data);
    this.setState({ lastRefreshManagersTime: new Date().getTime() });
    return response;
  };

  private deleteManagers = async (contestJid, data) => {
    const response = await this.props.onDeleteManagers(contestJid, data);
    this.setState({ lastRefreshManagersTime: new Date().getTime() });
    return response;
  };
}

export function createContestManagersPage(contestManagerActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetManagers: contestManagerActions.getManagers,
    onUpsertManagers: contestManagerActions.upsertManagers,
    onDeleteManagers: contestManagerActions.deleteManagers,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestManagersPage));
}

export default createContestManagersPage(injectedContestManagerActions);
