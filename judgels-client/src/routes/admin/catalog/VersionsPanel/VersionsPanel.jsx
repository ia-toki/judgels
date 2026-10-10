import { Alert, Button, HTMLTable, Intent } from '@blueprintjs/core';
import { History, Trash } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormattedRelative } from '../../../../components/FormattedRelative/FormattedRelative';
import { UserRef } from '../../../../components/UserRef/UserRef';
import VersionCommitForm from '../VersionCommitForm/VersionCommitForm';

import * as toastActions from '../../../../modules/toast/toastActions';

// The versions of a problem or a lesson: the actor's local changes, with commit, rebase and discard,
// and the history of committed versions, with restore.
export default function VersionsPanel({
  versions,
  profilesMap,
  hasLocalChanges,
  onCommitLocalChanges,
  onRebaseLocalChanges,
  onDiscardLocalChanges,
  onRestoreVersion,
}) {
  const [isRebasing, setIsRebasing] = useState(false);
  const [isDiscardAlertOpen, setIsDiscardAlertOpen] = useState(false);
  const [isDiscarding, setIsDiscarding] = useState(false);
  const [versionToRestore, setVersionToRestore] = useState(undefined);
  const [isRestoring, setIsRestoring] = useState(false);

  const commitLocalChanges = async data => {
    await onCommitLocalChanges({ title: data.title, description: data.description || '' });
    toastActions.showSuccessToast('Local changes committed.');
  };

  const rebaseLocalChanges = async () => {
    setIsRebasing(true);
    try {
      await onRebaseLocalChanges();
      toastActions.showSuccessToast('Local changes rebased.');
    } finally {
      setIsRebasing(false);
    }
  };

  const confirmDiscard = async () => {
    setIsDiscarding(true);
    try {
      await onDiscardLocalChanges();
      toastActions.showSuccessToast('Local changes discarded.');
    } finally {
      setIsDiscarding(false);
      setIsDiscardAlertOpen(false);
    }
  };

  const confirmRestore = async () => {
    setIsRestoring(true);
    try {
      await onRestoreVersion(versionToRestore.hash);
      toastActions.showSuccessToast('Version restored.');
    } finally {
      setIsRestoring(false);
      setVersionToRestore(undefined);
    }
  };

  const renderLocalChanges = () => {
    if (!hasLocalChanges) {
      return (
        <p>
          <small>No local changes.</small>
        </p>
      );
    }

    return (
      <>
        <VersionCommitForm onSubmit={commitLocalChanges} disabled={isRebasing} />
        <ActionButtons>
          <Button
            small
            text="Rebase on top of newer committed changes"
            loading={isRebasing}
            onClick={rebaseLocalChanges}
          />
          <Button
            small
            intent={Intent.DANGER}
            icon={<Trash />}
            text="Discard"
            disabled={isRebasing}
            onClick={() => setIsDiscardAlertOpen(true)}
          />
        </ActionButtons>
      </>
    );
  };

  const renderRestoreButton = (version, idx) => {
    // The latest version is the current one, and local changes were made on top of it.
    if (idx === 0 || hasLocalChanges) {
      return null;
    }
    return (
      <Button
        small
        intent={Intent.DANGER}
        text="Restore"
        aria-label={`Restore ${version.hash.substring(0, 7)}`}
        onClick={() => setVersionToRestore(version)}
      />
    );
  };

  return (
    <Flex flexDirection="column" gap={2}>
      <h4>Local changes</h4>
      {renderLocalChanges()}
      <h4>History</h4>
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th>Version</th>
            <th>Changes</th>
            <th>Author</th>
            <th>Time</th>
            <th />
          </tr>
        </thead>
        <tbody>
          {versions.map((version, idx) => (
            <tr key={version.hash}>
              <td>
                <code>{version.hash.substring(0, 7)}</code>
              </td>
              <td>
                {version.title}
                {version.description && (
                  <div style={{ whiteSpace: 'pre-wrap' }}>
                    <small>{version.description}</small>
                  </div>
                )}
              </td>
              <td>
                <UserRef profile={profilesMap[version.userJid]} />
              </td>
              <td>
                <FormattedRelative value={version.time} />
              </td>
              <td style={{ textAlign: 'right' }}>{renderRestoreButton(version, idx)}</td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
      <Alert
        isOpen={isDiscardAlertOpen}
        intent={Intent.DANGER}
        icon={<Trash />}
        confirmButtonText="Discard"
        cancelButtonText="Cancel"
        loading={isDiscarding}
        onConfirm={confirmDiscard}
        onCancel={() => setIsDiscardAlertOpen(false)}
      >
        Discard your local changes?
      </Alert>
      <Alert
        isOpen={versionToRestore !== undefined}
        intent={Intent.DANGER}
        icon={<History />}
        confirmButtonText="Restore"
        cancelButtonText="Cancel"
        loading={isRestoring}
        onConfirm={confirmRestore}
        onCancel={() => setVersionToRestore(undefined)}
      >
        Restore version {versionToRestore?.hash.substring(0, 7)}? This commits a new version with its content.
      </Alert>
    </Flex>
  );
}
