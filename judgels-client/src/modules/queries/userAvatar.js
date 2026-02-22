import { queryOptions } from '@tanstack/react-query';

import { userAvatarAPI } from '../api/jophiel/userAvatar';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const avatarExistsQueryOptions = userJid =>
  queryOptions({
    queryKey: ['user', userJid, 'avatar-exists'],
    queryFn: () => userAvatarAPI.avatarExists(userJid),
  });

export const avatarUrlQueryOptions = userJid =>
  queryOptions({
    queryKey: ['user', userJid, 'avatar-url'],
    queryFn: () => userAvatarAPI.renderAvatar(userJid),
  });

export const updateAvatarMutationOptions = userJid => ({
  mutationFn: file => userAvatarAPI.updateAvatar(getToken(), userJid, file),
  onSuccess: () => {
    queryClient.invalidateQueries(avatarExistsQueryOptions(userJid));
    queryClient.invalidateQueries(avatarUrlQueryOptions(userJid));
  },
});

export const deleteAvatarMutationOptions = userJid => ({
  mutationFn: () => userAvatarAPI.deleteAvatar(getToken(), userJid),
  onSuccess: () => {
    queryClient.invalidateQueries(avatarExistsQueryOptions(userJid));
    queryClient.invalidateQueries(avatarUrlQueryOptions(userJid));
  },
});
