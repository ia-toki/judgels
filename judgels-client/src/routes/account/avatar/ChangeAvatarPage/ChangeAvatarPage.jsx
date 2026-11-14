import { Button, Intent } from '@blueprintjs/core';
import { Box } from '@blueprintjs/labs';
import { useMutation, useQuery } from '@tanstack/react-query';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import {
  deleteUserAvatarMutationOptions,
  updateUserAvatarMutationOptions,
  userAvatarUrlQueryOptions,
  userHasAvatarQueryOptions,
} from '../../../../modules/queries/userAvatar';
import { useSession } from '../../../../modules/session';
import ChangeAvatarForm from './ChangeAvatarForm';

import * as toastActions from '../../../../modules/toast/toastActions';

import './ChangeAvatarPanel.scss';

export default function ChangeAvatarPage() {
  const { user } = useSession();
  const userJid = user.jid;

  const { data: hasAvatar } = useQuery(userHasAvatarQueryOptions(userJid));
  const { data: avatarUrl } = useQuery(userAvatarUrlQueryOptions(userJid));

  const deleteAvatarMutation = useMutation(deleteUserAvatarMutationOptions(userJid));
  const updateAvatarMutation = useMutation(updateUserAvatarMutationOptions(userJid));

  const deleteAvatar = () => {
    deleteAvatarMutation.mutate(undefined, {
      onSuccess: () => {
        toastActions.showSuccessToast('Avatar removed.');
      },
    });
    window.location.reload();
  };

  const uploadAvatar = data => {
    updateAvatarMutation.mutate(data.file, {
      onSuccess: () => {
        toastActions.showSuccessToast('Avatar updated.');
      },
    });
    window.location.reload();
  };

  if (!avatarUrl) {
    return <LoadingState />;
  }

  const currentAvatar = hasAvatar && (
    <Box className="change-avatar-panel__preview" flexGrow={1} marginBottom={4}>
      <h4>Current avatar</h4>
      <img src={avatarUrl} alt="avatar" />
      <div>
        <Button intent={Intent.DANGER} onClick={deleteAvatar}>
          Remove avatar
        </Button>
      </div>
    </Box>
  );

  const newAvatar = (
    <Box className="change-avatar-panel__preview" flexGrow={1} marginBottom={4}>
      <h4>Upload new avatar</h4>
      <ChangeAvatarForm onSubmit={uploadAvatar} />
    </Box>
  );

  return (
    <ContentCard title="Change avatar">
      <div className="change-avatar-panel__content">
        {currentAvatar}
        {newAvatar}
      </div>
    </ContentCard>
  );
}
