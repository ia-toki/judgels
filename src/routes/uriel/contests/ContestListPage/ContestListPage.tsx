import * as React from 'react';

import { contestListMock } from '../../../../modules/api/uriel/contest';
import { FullPageLayout } from '../../../../components/layouts/FullPageLayout/FullPageLayout';
import { ContestListTable } from 'components/ContestListTable/ContestListTable';
import { Pagination } from 'components/Pagination/Pagination';

export interface ContestListProps {}

export class ContestListPage extends React.Component<ContestListProps, {}> {
  render() {
    // assuming page is 1-indexed
    return (
      <FullPageLayout>
        <Pagination totalItems={contestListMock.totalItems} pageSize={10} currentPage={1} />
        <ContestListTable contestList={contestListMock} />
      </FullPageLayout>
    );
  }
}
