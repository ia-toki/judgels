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
import { isTrainingAdmin } from '../../../../modules/api/userRole';
import { lessonsQueryOptions } from '../../../../modules/queries/lesson';
import { userWebConfigQueryOptions } from '../../../../modules/queries/userWeb';

const PAGE_SIZE = 20;

export default function LessonsPage() {
  const location = useLocation();
  const term = location.search.term;
  const page = location.search.page;

  const { data: webConfig } = useQuery(userWebConfigQueryOptions());
  const { data: response, isFetching } = useQuery(lessonsQueryOptions({ term, page }));

  const canCreate = isTrainingAdmin(webConfig?.roles);

  const searchBoxUpdateQueries = (term, queries) => {
    return { ...queries, page: undefined, term };
  };

  const renderAction = () => {
    if (!canCreate) {
      return null;
    }
    return (
      <ButtonLink small intent={Intent.PRIMARY} to="/admin/lessons/-/new">
        <Plus />
        <span>New lesson</span>
      </ButtonLink>
    );
  };

  const renderSearchBox = () => {
    return <SearchBox onRouteChange={searchBoxUpdateQueries} initialValue={term || ''} isLoading={isFetching} />;
  };

  const renderLessons = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: lessons, profilesMap } = response;
    if (lessons.page.length === 0) {
      return (
        <p>
          <small>No lessons.</small>
        </p>
      );
    }

    const rows = lessons.page.map(lesson => (
      <tr key={lesson.jid}>
        <td className="col-fit">{lesson.id}</td>
        <td>
          <Link to={`/admin/lessons/${lesson.slug}`}>{lesson.slug}</Link>
        </td>
        <td>
          <UserRef profile={profilesMap[lesson.authorJid]} />
        </td>
        <td>
          <FormattedRelative value={lesson.lastUpdateTime} />
        </td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th className="col-fit">ID</th>
            <th>Slug</th>
            <th>Created by</th>
            <th>Last updated</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  return (
    <ContentCard title="Lessons" action={renderAction()}>
      {renderSearchBox()}
      {renderLessons()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
