import { Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';

import Pagination from '../../../../../../components/Pagination/Pagination';
import { Card } from '../../../../../../components/Card/Card';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { BlankContestListTable } from '../ContestListTable/BlankContestListTable';
import { ContestListTable } from '../ContestListTable/ContestListTable';
import { Contest, ContestPage } from '../../../../../../modules/api/uriel/contest';
import { contestActions as injectedContestActions } from '../modules/contestActions';

export interface ContestListPageProps extends RouteComponentProps<{}> {
  onFetchActiveContestList: () => Promise<Contest[]>;
  onFetchPastContestPage: (page: number) => Promise<ContestPage>;
}

export interface ContestListPageState {
  activeContestList?: Contest[];
  pastContestPage?: ContestPage;
}

class ContestListPage extends React.Component<ContestListPageProps, ContestListPageState> {
  private static PAGE_SIZE = 20;

  state: ContestListPageState = {};

  async componentDidMount() {
    const activeContestList = await this.props.onFetchActiveContestList();
    this.setState({ activeContestList });
  }

  render() {
    return (
      <div>
        {this.renderActiveContestList(this.state.activeContestList)}
        <Card title="Past contests">
          {this.renderPastContestPage(this.state.pastContestPage)}
          <Pagination currentPage={1} pageSize={ContestListPage.PAGE_SIZE} onChangePage={this.onChangePage} />
        </Card>
      </div>
    );
  }

  private renderActiveContestList = (contestList?: Contest[]) => {
    if (!contestList || contestList.length === 0) {
      return null;
    }
    return (
      <Card title="Active contests">
        <ContestListTable contestList={contestList} buttonIntent={Intent.PRIMARY} />
      </Card>
    );
  };

  private renderPastContestPage = (contestPage?: ContestPage) => {
    if (!contestPage) {
      return <BlankContestListTable />;
    }
    return <ContestListTable contestList={contestPage.data} buttonIntent={Intent.NONE} />;
  };

  private onChangePage = async (nextPage: number) => {
    const pastContestPage = await this.props.onFetchPastContestPage(nextPage);
    this.setState({ pastContestPage });
    return pastContestPage.totalData;
  };
}

export function createContestListPage(contestActions) {
  const mapDispatchToProps = {
    onFetchActiveContestList: contestActions.fetchActiveList,
    onFetchPastContestPage: contestActions.fetchPastPage,
  };
  return connect(undefined, mapDispatchToProps)(ContestListPage);
}

export default withBreadcrumb('Contests')(createContestListPage(injectedContestActions));
