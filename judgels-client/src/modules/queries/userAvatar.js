import { queryOptions } from '@tanstack/react-query';

import { userAvatarAPI } from '../api/userAvatar';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const userHasAvatarQueryOptions = userJid =>
  queryOptions({
    queryKey: ['user', userJid, 'avatar', 'exists'],
    queryFn: () => userAvatarAPI.hasAvatar(userJid),
  });

export const userAvatarUrlQueryOptions = userJid =>
  queryOptions({
    queryKey: ['user', userJid, 'avatar-url'],
    queryFn: () => userAvatarAPI.renderAvatar(userJid),
  });

export const updateUserAvatarMutationOptions = userJid => ({
  mutationFn: file => userAvatarAPI.updateAvatar(getToken(), userJid, file),
  onSuccess: () => {
    queryClient.invalidateQueries(userHasAvatarQueryOptions(userJid));
    queryClient.invalidateQueries(userAvatarUrlQueryOptions(userJid));
  },
});

export const deleteUserAvatarMutationOptions = userJid => ({
  mutationFn: () => userAvatarAPI.deleteAvatar(getToken(), userJid),
  onSuccess: () => {
    queryClient.invalidateQueries(userHasAvatarQueryOptions(userJid));
    queryClient.invalidateQueries(userAvatarUrlQueryOptions(userJid));
  },
});
