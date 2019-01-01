import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from 'components/ContentCard/ContentCard';
import { LoadingState } from 'components/LoadingState/LoadingState';
import Pagination from 'components/Pagination/Pagination';
import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';
import {
  ContestSupervisorsResponse,
  ContestSupervisorsDeleteResponse,
  ContestSupervisorsUpsertResponse,
  ContestSupervisorUpsertData,
} from 'modules/api/uriel/contestSupervisor';

import { ContestSupervisorsTable } from '../ContestSupervisorsTable/ContestSupervisorsTable';
import { ContestSupervisorAddDialog } from '../ContestSupervisorAddDialog/ContestSupervisorAddDialog';
import { ContestSupervisorRemoveDialog } from '../ContestSupervisorRemoveDialog/ContestSupervisorRemoveDialog';
import { selectContest } from '../../../modules/contestSelectors';
import { contestSupervisorActions as injectedContestSupervisorActions } from '../../modules/contestSupervisorActions';

import './ContestSupervisorsPage.css';

export interface ContestSupervisorsPageProps {
  contest: Contest;
  onGetSupervisors: (contestJid: string, page?: number) => Promise<ContestSupervisorsResponse>;
  onUpsertSupervisors: (
    contestJid: string,
    data: ContestSupervisorUpsertData
  ) => Promise<ContestSupervisorsUpsertResponse>;
  onDeleteSupervisors: (contestJid: string, usernames: string[]) => Promise<ContestSupervisorsDeleteResponse>;
}

interface ContestSupervisorsPageState {
  response?: ContestSupervisorsResponse;
  lastRefreshSupervisorsTime?: number;
}

class ContestSupervisorsPage extends React.Component<ContestSupervisorsPageProps, ContestSupervisorsPageState> {
  private static PAGE_SIZE = 100;

  state: ContestSupervisorsPageState = {};

  render() {
    return (
      <ContentCard>
        <h3>Supervisors</h3>
        <hr />
        {this.renderAddRemoveDialogs()}
        {this.renderSupervisors()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private renderSupervisors = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: supervisors, profilesMap } = response;
    if (supervisors.totalCount === 0) {
      return (
        <p>
          <small>No supervisors.</small>
        </p>
      );
    }

    return <ContestSupervisorsTable supervisors={supervisors.page} profilesMap={profilesMap} />;
  };

  private renderPagination = () => {
    // updates pagination when supervisors are refreshed
    const { lastRefreshSupervisorsTime } = this.state;
    const key = lastRefreshSupervisorsTime || 0;

    return (
      <Pagination
        key={key}
        currentPage={1}
        pageSize={ContestSupervisorsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  private onChangePage = async (nextPage: number) => {
    const data = await this.refreshSupervisors(nextPage);
    return data.totalCount;
  };

  private refreshSupervisors = async (page?: number) => {
    const response = await this.props.onGetSupervisors(this.props.contest.jid, page);
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
        <ContestSupervisorAddDialog contest={this.props.contest} onUpsertSupervisors={this.upsertSupervisors} />
        <ContestSupervisorRemoveDialog contest={this.props.contest} onDeleteSupervisors={this.deleteSupervisors} />
        <div className="clearfix" />
      </>
    );
  };

  private upsertSupervisors = async (contestJid, data) => {
    const response = await this.props.onUpsertSupervisors(contestJid, data);
    this.setState({ lastRefreshSupervisorsTime: new Date().getTime() });
    return response;
  };

  private deleteSupervisors = async (contestJid, data) => {
    const response = await this.props.onDeleteSupervisors(contestJid, data);
    this.setState({ lastRefreshSupervisorsTime: new Date().getTime() });
    return response;
  };
}

export function createContestSupervisorsPage(contestSupervisorActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetSupervisors: contestSupervisorActions.getSupervisors,
    onUpsertSupervisors: contestSupervisorActions.upsertSupervisors,
    onDeleteSupervisors: contestSupervisorActions.deleteSupervisors,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestSupervisorsPage));
}

export default createContestSupervisorsPage(injectedContestSupervisorActions);
