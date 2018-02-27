import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';

import Pagination from '../../../../../../../../components/Pagination/Pagination';
import { ContestListTable } from '../ContestListTable/ContestListTable';
import { withBreadcrumb } from '../../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { Card } from '../../../../../../../../components/Card/Card';
import { ContestList } from '../../../../../../../../modules/api/uriel/contest';
import { contestActions as injectedContestActions } from '../../../../../modules/contestActions';

export interface ContestListPageProps extends RouteComponentProps<{}> {
  onFetchContestList: (page: number) => Promise<ContestList>;
}

export interface ContestListPageState {
  contestList?: ContestList;
}

class ContestListPage extends React.Component<ContestListPageProps, ContestListPageState> {
  state: ContestListPageState = {};

  async componentDidMount() {
    await this.onChangePage(1);
  }

  render() {
    const { contestList } = this.state;
    if (!contestList) {
      return null;
    }

    return (
      <Card title="Past contests">
        <Pagination
          currentPage={1}
          pageSize={contestList.pageSize}
          totalData={contestList.totalData}
          onChangePage={this.onChangePage}
        />
        <ContestListTable contestList={contestList} />
      </Card>
    );
  }

  private onChangePage = async (nextPage: number) => {
    const contestList = await this.props.onFetchContestList(nextPage);
    this.setState({ contestList });
  };
}

export function createContestListPage(contestActions) {
  const mapDispatchToProps = {
    onFetchContestList: contestActions.fetchList,
  };
  return connect(undefined, mapDispatchToProps)(ContestListPage);
}

export default withBreadcrumb('Contests')(createContestListPage(injectedContestActions));
