import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';
import { Link } from '@tanstack/react-router';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { chaptersQueryOptions } from '../../../../modules/queries/chapter';
import { ChapterCreateDialog } from '../ChapterCreateDialog/ChapterCreateDialog';

export default function ChaptersPage() {
  const { data: response } = useQuery(chaptersQueryOptions());

  const renderChapters = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: chapters } = response;
    if (chapters.length === 0) {
      return (
        <p>
          <small>No chapters.</small>
        </p>
      );
    }

    const rows = chapters.map(chapter => (
      <tr key={chapter.jid}>
        <td style={{ width: '60px' }}>{chapter.id}</td>
        <td style={{ width: '200px' }}>
          <Link to={`/admin/chapters/${chapter.jid}`}>{chapter.jid}</Link>
        </td>
        <td>{chapter.name}</td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th style={{ width: '60px' }}>ID</th>
            <th style={{ width: '200px' }}>JID</th>
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
        <ChapterCreateDialog />
      </ActionButtons>
    );
  };

  return (
    <ContentCard title="Chapters">
      {renderAction()}
      {renderChapters()}
    </ContentCard>
  );
}
