import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';

import Pagination from 'components/Pagination/Pagination';
import { Card } from 'components/Card/Card';
import { Contest, ContestConfig, ContestCreateData, ContestPage } from 'modules/api/uriel/contest';

import { LoadingContestCard } from '../ContestCard/LoadingContestCard';
import { ContestCard } from '../ContestCard/ContestCard';
import { ContestCreateDialog } from '../ContestCreateDialog/ContestCreateDialog';
import { contestActions as injectedContestActions } from '../modules/contestActions';

export interface ContestsPageProps extends RouteComponentProps<{}> {
  onGetContests: (page?: number) => Promise<ContestPage>;
  onGetContestConfig: () => Promise<ContestConfig>;
  onCreateContest: (data: ContestCreateData) => Promise<Contest>;
}

export interface ContestsPageState {
  contests?: ContestPage;
}

class ContestsPage extends React.Component<ContestsPageProps, ContestsPageState> {
  private static PAGE_SIZE = 20;

  state: ContestsPageState = {};

  render() {
    return (
      <Card title="All contests">
        {this.renderCreateDialog()}
        {this.renderContests(this.state.contests)}
        <Pagination currentPage={1} pageSize={ContestsPage.PAGE_SIZE} onChangePage={this.onChangePage} />
      </Card>
    );
  }

  private renderCreateDialog = () => {
    return (
      <ContestCreateDialog
        onGetContestConfig={this.props.onGetContestConfig}
        onCreateContest={this.props.onCreateContest}
      />
    );
  };

  private renderContests = (contests?: ContestPage) => {
    if (!contests) {
      return <LoadingContestCard />;
    }
    return contests.page.map(contest => <ContestCard key={contest.jid} contest={contest} />);
  };

  private onChangePage = async (nextPage: number) => {
    const contests = await this.props.onGetContests(nextPage);
    this.setState({ contests });
    return contests.totalCount;
  };
}

export function createContestsPage(contestActions) {
  const mapDispatchToProps = {
    onGetContests: contestActions.getContests,
    onGetContestConfig: contestActions.getContestConfig,
    onCreateContest: contestActions.createContest,
  };
  return connect(undefined, mapDispatchToProps)(ContestsPage);
}

export default createContestsPage(injectedContestActions);
