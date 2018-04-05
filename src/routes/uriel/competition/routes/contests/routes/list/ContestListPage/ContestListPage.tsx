import { Spinner } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';

import Pagination from '../../../../../../../../components/Pagination/Pagination';
import { ContestListTable } from '../ContestListTable/ContestListTable';
import { withBreadcrumb } from '../../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { Card } from '../../../../../../../../components/Card/Card';
import { ContestList } from '../../../../../../../../modules/api/uriel/contest';
import { contestActions as injectedContestActions } from '../../../modules/contestActions';

export interface ContestListPageProps extends RouteComponentProps<{}> {
  onFetchContestList: (page: number, pageSize: number) => Promise<ContestList>;
}

export interface ContestListPageState {
  contestList?: ContestList;
}

class ContestListPage extends React.Component<ContestListPageProps, ContestListPageState> {
  private static PAGE_SIZE = 20;

  state: ContestListPageState = {};

  render() {
    return (
      <Card title="Past contests">
        <Pagination currentPage={1} pageSize={ContestListPage.PAGE_SIZE} onChangePage={this.onChangePage} />
        {this.renderContestList(this.state.contestList)}
      </Card>
    );
  }

  private renderContestList(contestList?: ContestList) {
    if (!contestList) {
      return <Spinner className="loading-spinner" />;
    }
    return <ContestListTable contestList={contestList} />;
  }

  private onChangePage = async (nextPage: number) => {
    const contestList = await this.props.onFetchContestList(nextPage, ContestListPage.PAGE_SIZE);
    this.setState({ contestList });
    return contestList.totalData;
  };
}

export function createContestListPage(contestActions) {
  const mapDispatchToProps = {
    onFetchContestList: contestActions.fetchList,
  };
  return connect(undefined, mapDispatchToProps)(ContestListPage);
}

export default withBreadcrumb('Contests')(createContestListPage(injectedContestActions));
