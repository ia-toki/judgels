import { useQuery } from '@tanstack/react-query';
import { useLocation } from '@tanstack/react-router';

import { Card } from '../../../../components/Card/Card';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import SearchBox from '../../../../components/SearchBox/SearchBox';
import { contestsQueryOptions } from '../../../../modules/queries/contest';
import { ContestCard } from '../ContestCard/ContestCard';
import { ContestCreateDialog } from '../ContestCreateDialog/ContestCreateDialog';

const PAGE_SIZE = 20;

export default function ContestsPage() {
  const location = useLocation();

  const name = location.search.name;
  const page = +(location.search.page || 1);

  const { data: response, isLoading } = useQuery(contestsQueryOptions({ name, page }));

  const renderHeader = () => {
    return (
      <>
        <div className="float-left">{renderCreateDialog()}</div>
        <div className="float-right">{renderFilter()}</div>
        <div className="clearfix" />
        {renderFilterResultsBanner()}
      </>
    );
  };

  const renderFilterResultsBanner = () => {
    if (!name) {
      return null;
    }

    return (
      <div className="content-card__section">
        Search results for: <b>{name}</b>
        <hr />
      </div>
    );
  };

  const renderFilter = () => {
    return (
      <SearchBox onRouteChange={searchBoxUpdateQueries} initialValue={name || ''} isLoading={isLoading && !!name} />
    );
  };

  const renderCreateDialog = () => {
    if (!response) {
      return null;
    }
    const config = response.config;
    if (!config.canAdminister) {
      return null;
    }
    return <ContestCreateDialog />;
  };

  const renderContests = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const rolesMap = response.rolesMap;
    const contests = response.data;
    if (!contests) {
      return <LoadingContentCard />;
    }

    if (contests.page.length === 0) {
      return (
        <p>
          <small>No contests.</small>
        </p>
      );
    }

    return contests.page.map(contest => (
      <ContestCard key={contest.jid} contest={contest} role={rolesMap[contest.jid]} />
    ));
  };

  const renderPagination = () => {
    if (!response) {
      return null;
    }
    return <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />;
  };

  const searchBoxUpdateQueries = (name, queries) => {
    return { ...queries, page: undefined, name };
  };

  return (
    <Card title="Contests">
      {renderHeader()}
      {renderContests()}
      {renderPagination()}
    </Card>
  );
}
