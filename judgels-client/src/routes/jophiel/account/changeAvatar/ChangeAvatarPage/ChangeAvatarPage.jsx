import { Button, Intent } from '@blueprintjs/core';
import { useMutation, useQuery } from '@tanstack/react-query';

import { Card } from '../../../../../components/Card/Card';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import {
  avatarExistsQueryOptions,
  avatarUrlQueryOptions,
  deleteAvatarMutationOptions,
  updateAvatarMutationOptions,
} from '../../../../../modules/queries/userAvatar';
import { useSession } from '../../../../../modules/session';
import ChangeAvatarForm from './ChangeAvatarForm';

import * as toastActions from '../../../../../modules/toast/toastActions';

import './ChangeAvatarPanel.scss';

export default function ChangeAvatarPage() {
  const { user } = useSession();
  const userJid = user.jid;

  const { data: avatarExists } = useQuery(avatarExistsQueryOptions(userJid));
  const { data: avatarUrl } = useQuery(avatarUrlQueryOptions(userJid));

  const deleteAvatarMutation = useMutation(deleteAvatarMutationOptions(userJid));
  const updateAvatarMutation = useMutation(updateAvatarMutationOptions(userJid));

  const deleteAvatar = async () => {
    await deleteAvatarMutation.mutateAsync(undefined, {
      onSuccess: () => {
        toastActions.showSuccessToast('Avatar removed.');
      },
    });
    window.location.reload();
  };

  const uploadAvatar = async data => {
    await updateAvatarMutation.mutateAsync(data.file, {
      onSuccess: () => {
        toastActions.showSuccessToast('Avatar updated.');
      },
    });
    window.location.reload();
  };

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
}
