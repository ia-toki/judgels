import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';

import Pagination from '../../../../../../components/Pagination/Pagination';
import { Card } from '../../../../../../components/Card/Card';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { LoadingContestCard } from '../ContestCard/LoadingContestCard';
import { ContestCard } from '../ContestCard/ContestCard';
import { Contest, ContestPage } from '../../../../../../modules/api/uriel/contest';
import { contestActions as injectedContestActions } from '../modules/contestActions';

export interface ContestsPageProps extends RouteComponentProps<{}> {
  onGetActiveContests: () => Promise<Contest[]>;
  onGetPastContests: (page: number) => Promise<ContestPage>;
}

export interface ContestsPageState {
  activeContests?: Contest[];
  pastContests?: ContestPage;
}

class ContestsPage extends React.Component<ContestsPageProps, ContestsPageState> {
  private static PAGE_SIZE = 20;

  state: ContestsPageState = {};

  async componentDidMount() {
    const activeContests = await this.props.onGetActiveContests();
    this.setState({ activeContests });
  }

  render() {
    return (
      <div>
        {this.renderActiveContests(this.state.activeContests)}
        <Card title="Past contests">
          {this.renderPastContests(this.state.pastContests)}
          <Pagination currentPage={1} pageSize={ContestsPage.PAGE_SIZE} onChangePage={this.onChangePage} />
        </Card>
      </div>
    );
  }

  private renderActiveContests = (contests?: Contest[]) => {
    if (!contests || contests.length === 0) {
      return null;
    }
    return (
      <Card title="Active contests">
        {contests.map(contest => <ContestCard key={contest.jid} contest={contest} />)}
      </Card>
    );
  };

  private renderPastContests = (contests?: ContestPage) => {
    if (!contests || !this.state.activeContests) {
      return <LoadingContestCard />;
    }
    return contests.data.map(contest => <ContestCard key={contest.jid} contest={contest} />);
  };

  private onChangePage = async (nextPage: number) => {
    const pastContests = await this.props.onGetPastContests(nextPage);
    this.setState({ pastContests });
    return pastContests.totalData;
  };
}

export function createContestsPage(contestActions) {
  const mapDispatchToProps = {
    onGetActiveContests: contestActions.getActiveContests,
    onGetPastContests: contestActions.getPastContests,
  };
  return connect(undefined, mapDispatchToProps)(ContestsPage);
}

export default withBreadcrumb('Contests')(createContestsPage(injectedContestActions));
