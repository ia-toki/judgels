import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from 'components/ContentCard/ContentCard';
import { LoadingState } from 'components/LoadingState/LoadingState';
import Pagination from 'components/Pagination/Pagination';
import { Contest } from 'modules/api/uriel/contest';
import { ContestContestantsResponse } from 'modules/api/uriel/contestContestant';

import { ContestContestantsTable } from '../ContestContestantsTable/ContestContestantsTable';
import { selectContest } from '../../../modules/contestSelectors';
import { contestContestantActions as injectedContestContestantActions } from '../../modules/contestContestantActions';

import { AppState } from 'modules/store';

export interface ContestContestantsPageProps {
  contest: Contest;
  onGetContestants: (contestJid: string, page?: number) => Promise<ContestContestantsResponse>;
}

interface ContestContestantsPageState {
  response?: ContestContestantsResponse;
  lastRefreshContestantsTime?: number;
}

class ContestContestantsPage extends React.Component<ContestContestantsPageProps, ContestContestantsPageState> {
  private static PAGE_SIZE = 100;

  state: ContestContestantsPageState = {};

  render() {
    return (
      <ContentCard>
        <h3>Contestants</h3>
        <hr />
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
}

export function createContestContestantsPage(contestContestantActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetContestants: contestContestantActions.getContestants,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestContestantsPage));
}

export default createContestContestantsPage(injectedContestContestantActions);
