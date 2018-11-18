import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';

import Pagination from 'components/Pagination/Pagination';
import { Card } from 'components/Card/Card';
import { Contest, ContestCreateData, ContestsResponse } from 'modules/api/uriel/contest';

import { LoadingContestCard } from '../ContestCard/LoadingContestCard';
import { ContestCard } from '../ContestCard/ContestCard';
import { ContestCreateDialog } from '../ContestCreateDialog/ContestCreateDialog';
import { contestActions as injectedContestActions } from '../modules/contestActions';

export interface ContestsPageProps extends RouteComponentProps<{}> {
  onGetContests: (page?: number) => Promise<ContestsResponse>;
  onCreateContest: (data: ContestCreateData) => Promise<Contest>;
}

export interface ContestsPageState {
  response?: ContestsResponse;
}

class ContestsPage extends React.Component<ContestsPageProps, ContestsPageState> {
  private static PAGE_SIZE = 20;

  state: ContestsPageState = {};

  render() {
    return (
      <Card title="Contests">
        {this.renderCreateDialog()}
        {this.renderContests()}
        {this.renderPagination()}
      </Card>
    );
  }

  private renderCreateDialog = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    const { config } = response;
    if (!config.canAdminister) {
      return null;
    }
    return <ContestCreateDialog onCreateContest={this.props.onCreateContest} />;
  };

  private renderContests = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingContestCard />;
    }

    const { data: contests } = response;
    return contests.page.map(contest => <ContestCard key={contest.jid} contest={contest} />);
  };

  private renderPagination = () => {
    return <Pagination currentPage={1} pageSize={ContestsPage.PAGE_SIZE} onChangePage={this.onChangePage} />;
  };

  private onChangePage = async (nextPage: number) => {
    const response = await this.props.onGetContests(nextPage);
    this.setState({ response });
    return response.data.totalCount;
  };
}

export function createContestsPage(contestActions) {
  const mapDispatchToProps = {
    onGetContests: contestActions.getContests,
    onCreateContest: contestActions.createContest,
  };
  return connect(undefined, mapDispatchToProps)(ContestsPage);
}

export default createContestsPage(injectedContestActions);
