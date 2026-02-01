import { Button, Intent } from '@blueprintjs/core';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { Card } from '../../../../../components/Card/Card';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { selectUserJid } from '../../../../../modules/session/sessionSelectors';
import ChangeAvatarForm from './ChangeAvatarForm';

import * as avatarActions from '../../../modules/avatarActions';

import './ChangeAvatarPanel.scss';

export default function ChangeAvatarPage() {
  const userJid = useSelector(selectUserJid);
  const dispatch = useDispatch();

  const [state, setState] = useState({
    avatarExists: undefined,
    avatarUrl: undefined,
  });

  const refreshAvatar = async () => {
    const [avatarExists, avatarUrl] = await Promise.all([
      dispatch(avatarActions.avatarExists(userJid)),
      dispatch(avatarActions.renderAvatar(userJid)),
    ]);
    setState(prevState => ({ ...prevState, avatarExists, avatarUrl }));
  };

  useEffect(() => {
    refreshAvatar();
  }, []);

  const render = () => {
    const { avatarExists, avatarUrl } = state;
    if (!avatarUrl) {
      return <LoadingState />;
    }

    const currentAvatar = avatarExists && (
      <div className="card-change-avatar__panel">
        <h4>Current avatar</h4>
        <img src={avatarUrl} alt="avatar" />
        <div>
          <Button intent={Intent.DANGER} onClick={deleteAvatar}>
            Remove avatar
          </Button>
        </div>
      </div>
    );

    const newAvatar = (
      <div className="card-change-avatar__panel">
        <h4>Upload new avatar</h4>
        <ChangeAvatarForm onSubmit={uploadAvatar} />
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

  const deleteAvatar = async () => {
    await dispatch(avatarActions.deleteAvatar(userJid));
    window.location.reload();
  };

  const uploadAvatar = async data => {
    await dispatch(avatarActions.updateAvatar(userJid, data.file));
    window.location.reload();
  };

  return render();
}
