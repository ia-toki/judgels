import { HTMLTable, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useQuery } from '@tanstack/react-query';
import { Link, useLocation } from '@tanstack/react-router';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import { contestsQueryOptions } from '../../../../modules/queries/contest';

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
        <td className="col-fit">{contest.id}</td>
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
            <th className="col-fit">ID</th>
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
        <ButtonLink intent={Intent.PRIMARY} to="/admin/contests/-/new">
          <Plus />
          <span>New contest</span>
        </ButtonLink>
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
