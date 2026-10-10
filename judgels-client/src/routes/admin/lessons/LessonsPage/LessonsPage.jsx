import { HTMLTable } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useQuery } from '@tanstack/react-query';
import { Link, useLocation } from '@tanstack/react-router';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import SearchBox from '../../../../components/SearchBox/SearchBox';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { TrainingAdminRole } from '../../../../modules/api/trainingAdminRole';
import { lessonsQueryOptions } from '../../../../modules/queries/lesson';
import { userWebConfigQueryOptions } from '../../../../modules/queries/userWeb';
import { LessonCreateDialog } from '../LessonCreateDialog/LessonCreateDialog';

const PAGE_SIZE = 20;

export default function LessonsPage() {
  const location = useLocation();
  const term = location.search.term;
  const page = location.search.page;

  const { data: webConfig } = useQuery(userWebConfigQueryOptions());
  const { data: response, isFetching } = useQuery(lessonsQueryOptions({ term, page }));

  const canCreate = webConfig?.role.training === TrainingAdminRole.Admin;

  const searchBoxUpdateQueries = (term, queries) => {
    return { ...queries, page: undefined, term };
  };

  const renderAction = () => {
    return (
      <Flex justifyContent="space-between" alignItems="center" gap={2}>
        <ActionButtons>{canCreate && <LessonCreateDialog />}</ActionButtons>
        <SearchBox onRouteChange={searchBoxUpdateQueries} initialValue={term || ''} isLoading={isFetching} />
      </Flex>
    );
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
        <td style={{ width: '60px' }}>{lesson.id}</td>
        <td>
          <Link to={`/admin/lessons/${lesson.jid}`}>{lesson.slug}</Link>
        </td>
        <td style={{ width: '200px' }}>
          <UserRef profile={profilesMap[lesson.authorJid]} />
        </td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th style={{ width: '60px' }}>ID</th>
            <th>Slug</th>
            <th style={{ width: '200px' }}>Author</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  return (
    <ContentCard title="Lessons">
      {renderAction()}
      {renderLessons()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
