import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { archivesQueryOptions } from '../../../../modules/queries/archive';
import { ArchiveCreateDialog } from '../ArchiveCreateDialog/ArchiveCreateDialog';
import { ArchiveEditDialog } from '../ArchiveEditDialog/ArchiveEditDialog';
import { ArchivesTable } from '../ArchivesTable/ArchivesTable';

export default function ArchivesPage() {
  const [editedArchive, setEditedArchive] = useState(undefined);

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

    return <ArchivesTable archives={archives} onEditArchive={setEditedArchive} />;
  };

  return (
    <ContentCard>
      <h3>Archives</h3>
      <hr />
      <ArchiveCreateDialog />
      <ArchiveEditDialog
        isOpen={!!editedArchive}
        archive={editedArchive}
        onCloseDialog={() => setEditedArchive(undefined)}
      />
      {renderArchives()}
    </ContentCard>
  );
}
