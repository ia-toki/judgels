import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';
import { Link, useLocation } from '@tanstack/react-router';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import { contestsQueryOptions } from '../../../../modules/queries/contest';
import { ContestCreateDialog } from '../../../contests/contests/ContestCreateDialog/ContestCreateDialog';

const PAGE_SIZE = 20;

export default function ContestsPage() {
  const location = useLocation();
  const page = location.search.page;

  const { data: response } = useQuery(contestsQueryOptions({ page }));

  const renderContests = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const contests = response.data.page;
    if (contests.length === 0) {
      return (
        <p>
          <small>No contests.</small>
        </p>
      );
    }

    const rows = contests.map(contest => (
      <tr key={contest.jid}>
        <td style={{ width: '60px' }}>{contest.id}</td>
        <td style={{ width: '200px' }}>
          <Link to={`/contests/${contest.slug}`}>{contest.slug}</Link>
        </td>
        <td>{contest.name}</td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th style={{ width: '60px' }}>ID</th>
            <th style={{ width: '200px' }}>Slug</th>
            <th>Name</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  const renderAction = () => {
    return (
      <ActionButtons>
        <ContestCreateDialog />
      </ActionButtons>
    );
  };

  const renderPagination = () => {
    if (!response) {
      return null;
    }
    return <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />;
  };

  return (
    <ContentCard title="Contests">
      {renderAction()}
      {renderContests()}
      {renderPagination()}
    </ContentCard>
  );
}
