import { queryOptions } from '@tanstack/react-query';

import { settingAPI } from '../api/setting';
import { queryClient } from '../queryClient';
import { getToken } from '../session';
import { userWebConfigQueryOptions } from './userWeb';

export const settingsQueryOptions = () =>
  queryOptions({
    queryKey: ['settings'],
    queryFn: () => settingAPI.getSettings(getToken()),
  });

export const updateAppSettingsMutationOptions = () => ({
  mutationFn: data => settingAPI.updateAppSettings(getToken(), data),
  onSuccess: () => {
    queryClient.invalidateQueries(settingsQueryOptions());
    queryClient.invalidateQueries(userWebConfigQueryOptions());
  },
});
