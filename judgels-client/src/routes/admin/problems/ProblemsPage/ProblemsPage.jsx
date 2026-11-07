import { HTMLTable, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useQuery } from '@tanstack/react-query';
import { Link, useLocation } from '@tanstack/react-router';

import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { FormattedRelative } from '../../../../components/FormattedRelative/FormattedRelative';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import SearchBox from '../../../../components/SearchBox/SearchBox';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { problemTypeNamesMap } from '../../../../modules/api/problem';
import { isProblemAdmin } from '../../../../modules/api/userRole';
import { problemsQueryOptions } from '../../../../modules/queries/problem';
import { userWebConfigQueryOptions } from '../../../../modules/queries/userWeb';

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

  const canCreate = isProblemAdmin(webConfig?.roles);

  const searchBoxUpdateQueries = (term, queries) => {
    return { ...queries, page: undefined, term };
  };

  const renderAction = () => {
    if (!canCreate) {
      return null;
    }
    return (
      <ButtonLink small intent={Intent.PRIMARY} to="/admin/problems/-/new">
        <Plus />
        <span>New problem</span>
      </ButtonLink>
    );
  };

  const renderSearchBox = () => {
    return <SearchBox onRouteChange={searchBoxUpdateQueries} initialValue={term || ''} isLoading={isFetching} />;
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
        <td className="col-fit">{problem.id}</td>
        <td>
          <Link to={`/admin/problems/${problem.slug}`}>{problem.slug}</Link>
        </td>
        <td>{problemTypeNamesMap[problem.type]}</td>
        <td>
          <UserRef profile={profilesMap[problem.authorJid]} />
        </td>
        <td>
          <FormattedRelative value={problem.lastUpdateTime} />
        </td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th className="col-fit">ID</th>
            <th>Slug</th>
            <th>Type</th>
            <th>Created by</th>
            <th>Last updated</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  return (
    <ContentCard title="Problems" action={renderAction()}>
      {renderSearchBox()}
      {renderProblems()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
