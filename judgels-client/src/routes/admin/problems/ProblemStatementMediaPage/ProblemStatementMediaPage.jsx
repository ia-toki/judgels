import { Alert, Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Download, Trash } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import prettyBytes from 'pretty-bytes';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormattedRelative } from '../../../../components/FormattedRelative/FormattedRelative';
import { problemStatementAPI } from '../../../../modules/api/problemStatement';
import { problemQueryOptions } from '../../../../modules/queries/problem';
import {
  deleteProblemStatementMediaFileMutationOptions,
  deleteProblemStatementMediaFilesMutationOptions,
  problemStatementMediaFilesQueryOptions,
  uploadProblemStatementMediaFileMutationOptions,
  uploadProblemStatementMediaZipMutationOptions,
} from '../../../../modules/queries/problemStatement';
import { getToken } from '../../../../modules/session';
import ProblemStatementMediaUploadForm from '../ProblemStatementMediaUploadForm/ProblemStatementMediaUploadForm';

import * as toastActions from '../../../../modules/toast/toastActions';

const ALL_FILES = Symbol('all files');

export default function ProblemStatementMediaPage() {
  const { problemJid } = useParams({ strict: false });

  const {
    data: { config },
  } = useSuspenseQuery(problemQueryOptions(problemJid));

  const {
    data: { data: files },
  } = useSuspenseQuery(problemStatementMediaFilesQueryOptions(problemJid));

  const uploadFileMutation = useMutation(uploadProblemStatementMediaFileMutationOptions(problemJid));
  const uploadZipMutation = useMutation(uploadProblemStatementMediaZipMutationOptions(problemJid));
  const deleteFileMutation = useMutation(deleteProblemStatementMediaFileMutationOptions(problemJid));
  const deleteFilesMutation = useMutation(deleteProblemStatementMediaFilesMutationOptions(problemJid));

  // Either a filename, or ALL_FILES.
  const [deleteTarget, setDeleteTarget] = useState(undefined);

  const uploadFile = async data => {
    const mutation = data.isZip ? uploadZipMutation : uploadFileMutation;
    await mutation.mutateAsync(data.file, {
      onSuccess: () => toastActions.showSuccessToast(data.isZip ? 'Files uploaded.' : 'File uploaded.'),
    });
  };

  const confirmDelete = async () => {
    if (deleteTarget === ALL_FILES) {
      await deleteFilesMutation.mutateAsync(undefined, {
        onSuccess: () => toastActions.showSuccessToast('All files deleted.'),
      });
    } else {
      await deleteFileMutation.mutateAsync(deleteTarget, {
        onSuccess: () => toastActions.showSuccessToast('File deleted.'),
      });
    }
    setDeleteTarget(undefined);
  };

  const renderDeleteAllButton = () => {
    return (
      config.canEdit &&
      files.length > 0 && (
        <Button small intent={Intent.DANGER} icon={<Trash />} onClick={() => setDeleteTarget(ALL_FILES)}>
          Delete all
        </Button>
      )
    );
  };

  const renderFiles = () => {
    if (files.length === 0) {
      return (
        <p>
          <small>No files.</small>
        </p>
      );
    }

    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th>Filename</th>
            <th>Size</th>
            <th>Last modified</th>
            <th />
          </tr>
        </thead>
        <tbody>
          {files.map(file => (
            <tr key={file.name}>
              <td>{file.name}</td>
              <td>{prettyBytes(file.size)}</td>
              <td>
                <FormattedRelative value={file.lastModifiedTime} />
              </td>
              <td>
                <ActionButtons justifyContent="end">
                  <Button
                    small
                    minimal
                    icon={<Download />}
                    aria-label={`Download ${file.name}`}
                    onClick={() => problemStatementAPI.downloadStatementMediaFile(getToken(), problemJid, file.name)}
                  />
                  {config.canEdit && (
                    <Button
                      small
                      minimal
                      intent={Intent.DANGER}
                      icon={<Trash />}
                      aria-label={`Delete ${file.name}`}
                      onClick={() => setDeleteTarget(file.name)}
                    />
                  )}
                </ActionButtons>
              </td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
    );
  };

  return (
    <Flex flexDirection="column" gap={2}>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Media</span>
          {renderDeleteAllButton()}
        </h4>
      </Flex>
      <p>
        <small>
          Refer to a media file in a statement as <code>render/&lt;filename&gt;</code>. Previews show a newly uploaded
          file only after it is committed.
        </small>
      </p>
      {config.canEdit && <ProblemStatementMediaUploadForm onSubmit={uploadFile} />}
      {renderFiles()}
      <Alert
        isOpen={deleteTarget !== undefined}
        intent={Intent.DANGER}
        icon={<Trash />}
        confirmButtonText="Delete"
        cancelButtonText="Cancel"
        loading={deleteFileMutation.isPending || deleteFilesMutation.isPending}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(undefined)}
      >
        {deleteTarget === ALL_FILES ? 'Delete all media files?' : `Delete ${deleteTarget}?`}
      </Alert>
    </Flex>
  );
}
