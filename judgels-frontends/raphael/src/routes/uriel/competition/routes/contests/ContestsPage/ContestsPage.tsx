import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';

import Pagination from '../../../../../../components/Pagination/Pagination';
import { Card } from '../../../../../../components/Card/Card';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { LoadingContestCard } from '../ContestCard/LoadingContestCard';
import { ContestCard } from '../ContestCard/ContestCard';
import { ContestPage } from '../../../../../../modules/api/uriel/contest';
import { contestActions as injectedContestActions } from '../modules/contestActions';

export interface ContestsPageProps extends RouteComponentProps<{}> {
  onGetContests: (page: number) => Promise<ContestPage>;
}

export interface ContestsPageState {
  contests?: ContestPage;
}

class ContestsPage extends React.Component<ContestsPageProps, ContestsPageState> {
  private static PAGE_SIZE = 20;

  state: ContestsPageState = {};

  render() {
    return (
      <Card title="Contests">
        {this.renderContests(this.state.contests)}
        <Pagination currentPage={1} pageSize={ContestsPage.PAGE_SIZE} onChangePage={this.onChangePage} />
      </Card>
    );
  }

  private renderContests = (contests?: ContestPage) => {
    if (!contests) {
      return <LoadingContestCard />;
    }
    return contests.data.map(contest => <ContestCard key={contest.jid} contest={contest} />);
  };

  private onChangePage = async (nextPage: number) => {
    const contests = await this.props.onGetContests(nextPage);
    this.setState({ contests });
    return contests.totalData;
  };
}

export function createContestsPage(contestActions) {
  const mapDispatchToProps = {
    onGetContests: contestActions.getContests,
  };
  return connect(undefined, mapDispatchToProps)(ContestsPage);
}

export default withBreadcrumb('Contests')(createContestsPage(injectedContestActions));
