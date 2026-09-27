import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';
import { useLocation } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import Pagination from '../../../../components/Pagination/Pagination';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { lessonsQueryOptions } from '../../../../modules/queries/lesson';

const PAGE_SIZE = 20;

export default function LessonsPage() {
  const location = useLocation();
  const page = location.search.page;

  const { data: response } = useQuery(lessonsQueryOptions({ page }));

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
        <td>{lesson.slug}</td>
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
      {renderLessons()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
