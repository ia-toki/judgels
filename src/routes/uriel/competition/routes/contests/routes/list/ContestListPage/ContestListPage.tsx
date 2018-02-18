import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';

import Pagination from '../../../../../../../../components/Pagination/Pagination';
import { ContestListTable } from '../ContestListTable/ContestListTable';
import { withBreadcrumb } from '../../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { Card } from '../../../../../../../../components/Card/Card';
import { ContestList } from '../../../../../../../../modules/api/uriel/contest';
import { contestActions as injectedContestActions } from '../../../../../modules/contestActions';

export interface ContestListPageProps {
  contestList: ContestList;
  onChangePage: (nextPage: number) => Promise<void>;
}

class ContestListPage extends React.Component<ContestListPageProps, {}> {
  render() {
    const { contestList } = this.props;
    return (
      <div>
        <Card title="Past contests">
          <Pagination
            currentPage={1}
            pageSize={contestList.pageSize}
            totalData={contestList.totalData}
            onChangePage={this.props.onChangePage}
          />
          <ContestListTable contestList={contestList} />
        </Card>
      </div>
    );
  }
}

interface ContestListPageContainerProps extends RouteComponentProps<{}> {
  onFetchContestList: (page: number) => Promise<ContestList>;
}

interface ContestListPageContainerState {
  contestList?: ContestList;
}

class ContestListPageContainer extends React.Component<ContestListPageContainerProps, ContestListPageContainerState> {
  state: ContestListPageContainerState = {};

  async componentDidMount() {
    const contestList = await this.props.onFetchContestList(1);
    this.setState({ contestList });
  }

  render() {
    if (!this.state.contestList) {
      return null;
    }
    return <ContestListPage contestList={this.state.contestList} onChangePage={this.onChangePage} />;
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
  return connect(undefined, mapDispatchToProps)(ContestListPageContainer);
}

export default withBreadcrumb('Contests')(createContestListPage(injectedContestActions));
