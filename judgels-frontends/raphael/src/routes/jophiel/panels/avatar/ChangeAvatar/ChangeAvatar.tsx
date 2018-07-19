import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import Dropzone from 'react-dropzone';

import { Card } from '../../../../../components/Card/Card';

import './ChangeAvatar.css';

export const MAX_AVATAR_FILE_SIZE = 100 * 1024;

export interface ChangeAvatarPanelProps {
  onDropAccepted: (files: File[]) => Promise<void>;
  onDropRejected: (files: File[]) => Promise<void>;
  onRenderAvatar: () => Promise<string>;
  onDeleteAvatar: () => Promise<void>;
}

interface ChangeAvatarPanelState {
  avatarUrl?: string;
}

export class ChangeAvatarPanel extends React.PureComponent<ChangeAvatarPanelProps, ChangeAvatarPanelState> {
  state: ChangeAvatarPanelState = {};

  async componentDidMount() {
    const avatarUrl = await this.props.onRenderAvatar();
    this.setState({ avatarUrl });
  }

  render() {
    const currentAvatar = this.state.avatarUrl && (
      <div className="card-change-avatar__panel">
        <h4>Current avatar</h4>
        <img src={this.state.avatarUrl} />
        <div>
          <Button intent={Intent.DANGER} onClick={this.props.onDeleteAvatar}>
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
          onDropAccepted={this.props.onDropAccepted}
          onDropRejected={this.props.onDropRejected}
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
  }
}
