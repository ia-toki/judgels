import { Button, Intent } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';

import { withBreadcrumb } from '../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { Card } from '../../../../../components/Card/Card';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { selectUserJid } from '../../../../../modules/session/sessionSelectors';
import ChangeAvatarForm from './ChangeAvatarForm';

import * as avatarActions from '../../../modules/avatarActions';

import './ChangeAvatarPanel.scss';

class ChangeAvatarPage extends Component {
  state = {
    avatarExists: undefined,
    avatarUrl: undefined,
  };

  async componentDidMount() {
    const [avatarExists, avatarUrl] = await Promise.all([this.avatarExists(), this.renderAvatar()]);
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
        <img src={avatarUrl} alt="avatar" />
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
        <ChangeAvatarForm onSubmit={this.uploadAvatar} />
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

  avatarExists = async () => {
    return await this.props.onAvatarExists(this.props.userJid);
  };

  renderAvatar = async () => {
    return await this.props.onRenderAvatar(this.props.userJid);
  };

  deleteAvatar = async () => {
    await this.props.onDeleteAvatar(this.props.userJid);
    window.location.reload();
  };

  uploadAvatar = async data => {
    await this.props.onUpdateAvatar(this.props.userJid, data.file);
    window.location.reload();
  };
}

const mapStateToProps = state => ({
  userJid: selectUserJid(state),
});
const mapDispatchToProps = {
  onAvatarExists: avatarActions.avatarExists,
  onRenderAvatar: avatarActions.renderAvatar,
  onDeleteAvatar: avatarActions.deleteAvatar,
  onUpdateAvatar: avatarActions.updateAvatar,
};
export default withBreadcrumb('Change avatar')(connect(mapStateToProps, mapDispatchToProps)(ChangeAvatarPage));
