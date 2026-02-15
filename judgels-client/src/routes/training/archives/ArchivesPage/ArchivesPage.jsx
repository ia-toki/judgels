import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { callAction } from '../../../../modules/callAction';
import { ArchiveCreateDialog } from '../ArchiveCreateDialog/ArchiveCreateDialog';
import { ArchiveEditDialog } from '../ArchiveEditDialog/ArchiveEditDialog';
import { ArchivesTable } from '../ArchivesTable/ArchivesTable';

import * as archiveActions from '../modules/archiveActions';

export default function ArchivesPage() {
  const [state, setState] = useState({
    response: undefined,
    isEditDialogOpen: false,
    editedArchive: undefined,
  });

  const refreshArchives = async () => {
    const response = await callAction(archiveActions.getArchives());
    setState(prevState => ({ ...prevState, response }));
  };

  useEffect(() => {
    refreshArchives();
  }, []);

  const render = () => {
    return (
      <ContentCard>
        <h3>Archives</h3>
        <hr />
        {renderCreateDialog()}
        {renderEditDialog()}
        {renderArchives()}
      </ContentCard>
    );
  };

  const renderCreateDialog = () => {
    return <ArchiveCreateDialog onCreateArchive={createArchive} />;
  };

  const renderEditDialog = () => {
    const { isEditDialogOpen, editedArchive } = state;
    return (
      <ArchiveEditDialog
        isOpen={isEditDialogOpen}
        archive={editedArchive}
        onUpdateArchive={updateArchive}
        onCloseDialog={() => editArchive(undefined)}
      />
    );
  };

  const renderArchives = () => {
    const { response } = state;
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

    return <ArchivesTable archives={archives} onEditArchive={editArchive} />;
  };

  const createArchive = async data => {
    await callAction(archiveActions.createArchive(data));
    await refreshArchives();
  };

  const editArchive = async archive => {
    setState(prevState => ({
      ...prevState,
      isEditDialogOpen: !!archive,
      editedArchive: archive,
    }));
  };

  const updateArchive = async (archiveJid, data) => {
    await callAction(archiveActions.updateArchive(archiveJid, data));
    editArchive(undefined);
    await refreshArchives();
  };

  return render();
}
