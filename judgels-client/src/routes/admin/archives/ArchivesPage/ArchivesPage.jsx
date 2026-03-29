import { HTMLTable } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';
import { Link } from '@tanstack/react-router';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { archivesQueryOptions } from '../../../../modules/queries/archive';
import { ArchiveCreateDialog } from '../ArchiveCreateDialog/ArchiveCreateDialog';

export default function ArchivesPage() {
  const { data: response } = useQuery(archivesQueryOptions());

  const renderArchives = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: archives } = response;
    if (archives.length === 0) {
      return (
        <p>
          <small>No archives.</small>
        </p>
      );
    }

    const rows = archives.map(archive => (
      <tr key={archive.jid}>
        <td style={{ width: '60px' }}>{archive.id}</td>
        <td style={{ width: '200px' }}>
          <Link to={`/admin/archives/${archive.slug}`}>{archive.slug}</Link>
        </td>
        <td>{archive.name}</td>
        <td>{archive.category}</td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th style={{ width: '60px' }}>ID</th>
            <th style={{ width: '200px' }}>Slug</th>
            <th>Name</th>
            <th>Category</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  const renderAction = () => {
    return (
      <ActionButtons>
        <ArchiveCreateDialog />
      </ActionButtons>
    );
  };

  return (
    <ContentCard title="Archives">
      {renderAction()}
      {renderArchives()}
    </ContentCard>
  );
}
