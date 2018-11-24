import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import Dropzone from 'react-dropzone';

import { Card } from 'components/Card/Card';
import { LoadingState } from 'components/LoadingState/LoadingState';

import { MAX_AVATAR_FILE_SIZE } from '../../../modules/avatarActions';

import './ChangeAvatarPanel.css';

export interface ChangeAvatarPanelProps {
  onDropAccepted: (files: File[]) => Promise<void>;
  onDropRejected: (files: File[]) => Promise<void>;
  onAvatarExists: () => Promise<boolean>;
  onRenderAvatar: () => Promise<string>;
  onDeleteAvatar: () => Promise<void>;
}

interface ChangeAvatarPanelState {
  avatarExists?: boolean;
  avatarUrl?: string;
}

export class ChangeAvatarPanel extends React.PureComponent<ChangeAvatarPanelProps, ChangeAvatarPanelState> {
  state: ChangeAvatarPanelState = {};

  async componentDidMount() {
    const [avatarExists, avatarUrl] = await Promise.all([this.props.onAvatarExists(), this.props.onRenderAvatar()]);
    this.setState({ avatarExists, avatarUrl });
  }

  render() {
    const { avatarExists, avatarUrl } = this.state;
    if (!avatarUrl) {
      return <LoadingState />;
    }

    const currentAvatar = avatarExists && (
      <div className="card-change-avatar__panel">
        <h4>Current avatar</h4>
        <img src={avatarUrl} />
        <div>
          <Button intent={Intent.DANGER} onClick={this.deleteAvatar}>
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
          onDropAccepted={this.dropAccepted}
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

  private dropAccepted = async files => {
    await this.props.onDropAccepted(files);
    window.location.reload();
  };

  private deleteAvatar = async () => {
    await this.props.onDeleteAvatar();
    window.location.reload();
  };
}
