import * as React from 'react';

import { Pagination } from '../../../../../../components/Pagination/Pagination';
import { contestListMock } from '../../../../../../modules/api/uriel/contest';
import { ContestListTable } from '../ContestListTable/ContestListTable';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { Card } from '../../../../../../components/Card/Card';

export interface ContestListProps {}

class ContestListPage extends React.Component<ContestListProps, {}> {
  render() {
    // assuming page is 1-indexed
    return (
      <Card title="Past contests">
        <Pagination totalItems={contestListMock.totalItems} pageSize={10} currentPage={1} />
        <ContestListTable contestList={contestListMock} />
      </Card>
    );
  }
}

export default withBreadcrumb('Contests')(ContestListPage);
