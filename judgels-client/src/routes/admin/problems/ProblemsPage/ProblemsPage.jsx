import { HTMLTable } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useQuery } from '@tanstack/react-query';
import { Link, useLocation } from '@tanstack/react-router';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import ProblemTagFilter from '../../../../components/ProblemTagFilter/ProblemTagFilter';
import SearchBox from '../../../../components/SearchBox/SearchBox';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { problemTypeNamesMap } from '../../../../modules/api/problem';
import { ProblemAdminRole } from '../../../../modules/api/problemAdminRole';
import { problemsQueryOptions } from '../../../../modules/queries/problem';
import { problemTagsQueryOptions } from '../../../../modules/queries/problemTag';
import { userWebConfigQueryOptions } from '../../../../modules/queries/userWeb';
import { ProblemCreateDialog } from '../ProblemCreateDialog/ProblemCreateDialog';

import './ProblemsPage.scss';

const PAGE_SIZE = 20;

const parseTags = queryTags => {
  if (!queryTags) {
    return undefined;
  }
  return typeof queryTags === 'string' ? [queryTags] : queryTags;
};

export default function ProblemsPage() {
  const location = useLocation();
  const term = location.search.term;
  const tags = parseTags(location.search.tags);
  const page = location.search.page;

  const { data: webConfig } = useQuery(userWebConfigQueryOptions());
  const { data: response, isFetching } = useQuery(problemsQueryOptions({ term, tags, page }));

  const canCreate = webConfig?.role.problem === ProblemAdminRole.Admin;

  const searchBoxUpdateQueries = (term, queries) => {
    return { ...queries, page: undefined, term };
  };

  const renderAction = () => {
    return (
      <Flex justifyContent="space-between" alignItems="center" gap={2}>
        <ActionButtons>{canCreate && <ProblemCreateDialog />}</ActionButtons>
        <SearchBox onRouteChange={searchBoxUpdateQueries} initialValue={term || ''} isLoading={isFetching} />
      </Flex>
    );
  };

  const renderProblems = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: problems, profilesMap } = response;
    if (problems.page.length === 0) {
      return (
        <p>
          <small>No problems.</small>
        </p>
      );
    }

    const rows = problems.page.map(problem => (
      <tr key={problem.jid}>
        <td style={{ width: '60px' }}>{problem.id}</td>
        <td>
          <Link to={`/admin/problems/${problem.slug}`}>{problem.slug}</Link>
        </td>
        <td style={{ width: '120px' }}>{problemTypeNamesMap[problem.type]}</td>
        <td style={{ width: '200px' }}>
          <UserRef profile={profilesMap[problem.authorJid]} />
        </td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th style={{ width: '60px' }}>ID</th>
            <th>Slug</th>
            <th style={{ width: '120px' }}>Type</th>
            <th style={{ width: '200px' }}>Author</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  return (
    <Flex className="admin-problems-page" alignItems="flex-start" gap={2}>
      <ContentCard className="admin-problems-page__problems" title="Problems">
        {renderAction()}
        {renderProblems()}
        {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
      </ContentCard>
      <div className="admin-problems-page__filter">
        <ProblemTagFilter queryOptions={problemTagsQueryOptions()} />
      </div>
    </Flex>
  );
}
