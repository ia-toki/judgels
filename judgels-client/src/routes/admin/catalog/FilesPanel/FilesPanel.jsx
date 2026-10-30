import { Alert, Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Download, Trash } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import prettyBytes from 'pretty-bytes';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormattedRelative } from '../../../../components/FormattedRelative/FormattedRelative';
import FileUploadForm from '../FileUploadForm/FileUploadForm';

import * as toastActions from '../../../../modules/toast/toastActions';

const ALL_FILES = Symbol('all files');

// One directory of a problem's or a lesson's files: its listing, with upload, download and delete.
export default function FilesPanel({
  title,
  description,
  noun,
  files,
  onUploadFile,
  onUploadZip,
  onDownloadFile,
  onDeleteFile,
  onDeleteFiles,
}) {
  // Either a filename, or ALL_FILES.
  const [deleteTarget, setDeleteTarget] = useState(undefined);
  const [isDeleting, setIsDeleting] = useState(false);

  const uploadFile = async data => {
    if (data.isZip) {
      await onUploadZip(data.file);
      toastActions.showSuccessToast('Files uploaded.');
    } else {
      await onUploadFile(data.file);
      toastActions.showSuccessToast('File uploaded.');
    }
  };

  const confirmDelete = async () => {
    setIsDeleting(true);
    try {
      if (deleteTarget === ALL_FILES) {
        await onDeleteFiles();
        toastActions.showSuccessToast('All files deleted.');
      } else {
        await onDeleteFile(deleteTarget);
        toastActions.showSuccessToast('File deleted.');
      }
    } finally {
      setIsDeleting(false);
      setDeleteTarget(undefined);
    }
  };

  const renderDeleteAllButton = () => {
    return (
      <Button small intent={Intent.DANGER} icon={<Trash />} onClick={() => setDeleteTarget(ALL_FILES)}>
        Delete all
      </Button>
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
      <Flex flexDirection="column" gap={2}>
        <Flex>{renderDeleteAllButton()}</Flex>
        <HTMLTable striped className="table-list-condensed">
          <thead>
            <tr>
              <th>Filename</th>
              <th>Size</th>
              <th>Last modified</th>
              <th className="col-fit" />
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
                <td className="col-fit">
                  <ActionButtons justifyContent="end">
                    <Button
                      small
                      minimal
                      icon={<Download />}
                      aria-label={`Download ${file.name}`}
                      onClick={() => onDownloadFile(file.name)}
                    />
                    <Button
                      small
                      minimal
                      intent={Intent.DANGER}
                      icon={<Trash />}
                      aria-label={`Delete ${file.name}`}
                      onClick={() => setDeleteTarget(file.name)}
                    />
                  </ActionButtons>
                </td>
              </tr>
            ))}
          </tbody>
        </HTMLTable>
      </Flex>
    );
  };

  return (
    <Flex flexDirection="column" gap={4}>
      <Flex flexDirection="column" gap={2}>
        <Flex flexDirection="column">
          <h4>{title}</h4>
          {description && (
            <p>
              <small>{description}</small>
            </p>
          )}
        </Flex>
        <FileUploadForm onSubmit={uploadFile} />
      </Flex>
      {renderFiles()}
      <Alert
        isOpen={deleteTarget !== undefined}
        intent={Intent.DANGER}
        icon={<Trash />}
        confirmButtonText="Delete"
        cancelButtonText="Cancel"
        loading={isDeleting}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(undefined)}
      >
        {deleteTarget === ALL_FILES ? `Delete all ${noun} files?` : `Delete ${deleteTarget}?`}
      </Alert>
    </Flex>
  );
}
