import { useQuery } from '@tanstack/react-query';
import { useLocation } from '@tanstack/react-router';

import { Card } from '../../../../components/Card/Card';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import SearchBox from '../../../../components/SearchBox/SearchBox';
import { problemSetsQueryOptions } from '../../../../modules/queries/problemSet';
import { ProblemSetCard } from '../ProblemSetCard/ProblemSetCard';

const PAGE_SIZE = 20;

export default function ProblemSetsPage() {
  const location = useLocation();

  const archiveSlug = location.search.archive;
  const name = location.search.name;
  const page = +(location.search.page || 1);

  const { data: response, isFetching } = useQuery(problemSetsQueryOptions({ archiveSlug, name, page }));

  const renderHeader = () => {
    return (
      <>
        <div className="float-right">{renderFilter()}</div>
        <div className="clearfix" />
        <div className="content-card__section">
          {renderFilterResultsBanner()}
          <hr />
        </div>
      </>
    );
  };

  const renderFilterResultsBanner = () => {
    if (!archiveSlug && !name) {
      return <>Most recently added problemsets:</>;
    }

    const archiveName = response && response.archiveName;

    if (archiveName && !name) {
      return (
        <>
          Problemsets in archive <b>{archiveName}</b>:
        </>
      );
    }
    if (!name) {
      return null;
    }

    const archiveNameResult = archiveName ? (
      <>
        {' '}
        in archive <b>{archiveName}:</b>
      </>
    ) : (
      ''
    );

    return (
      <>
        Search results for: <b>{name}</b>
        {archiveNameResult}
      </>
    );
  };

  const renderFilter = () => {
    return <SearchBox onRouteChange={searchBoxUpdateQueries} initialValue={name || ''} isLoading={isFetching} />;
  };

  const renderProblemSets = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data, archiveDescriptionsMap, problemSetProgressesMap, profilesMap } = response;
    if (!data) {
      return <LoadingContentCard />;
    }

    if (data.page.length === 0) {
      return (
        <p>
          <small>No problemsets.</small>
        </p>
      );
    }
    return data.page.map(problemSet => (
      <ProblemSetCard
        key={problemSet.jid}
        problemSet={problemSet}
        archiveDescription={archiveDescriptionsMap[problemSet.archiveJid]}
        progress={problemSetProgressesMap[problemSet.jid]}
        profilesMap={profilesMap}
      />
    ));
  };

  const searchBoxUpdateQueries = (name, queries) => {
    return { ...queries, page: undefined, name };
  };

  return (
    <Card title="Browse problemsets">
      {renderHeader()}
      {renderProblemSets()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </Card>
  );
}
