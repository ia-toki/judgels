import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import Dropzone from 'react-dropzone';

import { Card } from '../../../../../components/Card/Card';

import './ChangeAvatar.css';

export const MAX_AVATAR_FILE_SIZE = 100 * 1024;

export interface ChangeAvatarPanelProps {
  avatarUrl?: string;
  onDropAccepted: (files: File[]) => Promise<void>;
  onDropRejected: (files: File[]) => Promise<void>;
  onRemoveAvatar: () => Promise<void>;
}

export const ChangeAvatarPanel = (props: ChangeAvatarPanelProps) => {
  const currentAvatar = props.avatarUrl && (
    <div className="card-change-avatar__panel">
      <h4>Current avatar</h4>
      <img src={props.avatarUrl} />
      <div>
        <Button intent={Intent.DANGER} onClick={props.onRemoveAvatar}>
          Remove avatar
        </Button>
      </div>
    </div>
  );

  const newAvatar = (
    <div className="card-change-avatar__panel">
      <h4>Upload new avatar</h4>
      <Dropzone
        accept="image/*"
        multiple={false}
        maxSize={MAX_AVATAR_FILE_SIZE}
        onDropAccepted={props.onDropAccepted}
        onDropRejected={props.onDropRejected}
      >
        <div className="card-change-avatar__dropzone">Click here or drop a new image (max 100 KB).</div>
      </Dropzone>
    </div>
  );

  return (
    <Card title="Change avatar" className="card-change-avatar">
      <div className="card-change-avatar__content">
        {currentAvatar}
        {newAvatar}
      </div>
    </Card>
  );
};
