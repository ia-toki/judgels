import { queryOptions } from '@tanstack/react-query';

import { contestModuleAPI } from '../api/uriel/contestModule';
import { queryClient } from '../queryClient';
import { getToken } from '../session';
import { contestWebConfigQueryOptions } from './contestWeb';

export const contestModulesQueryOptions = contestJid =>
  queryOptions({
    queryKey: ['contest', contestJid, 'modules'],
    queryFn: () => contestModuleAPI.getModules(getToken(), contestJid),
  });

export const contestModuleConfigQueryOptions = contestJid =>
  queryOptions({
    queryKey: ['contest', contestJid, 'modules', 'config'],
    queryFn: () => contestModuleAPI.getConfig(getToken(), contestJid),
  });

export const enableContestModuleMutationOptions = contestJid => ({
  mutationFn: type => contestModuleAPI.enableModule(getToken(), contestJid, type),
  onSuccess: () => {
    queryClient.invalidateQueries(contestModulesQueryOptions(contestJid));
    queryClient.invalidateQueries(contestWebConfigQueryOptions(contestJid));
  },
});

export const disableContestModuleMutationOptions = contestJid => ({
  mutationFn: type => contestModuleAPI.disableModule(getToken(), contestJid, type),
  onSuccess: () => {
    queryClient.invalidateQueries(contestModulesQueryOptions(contestJid));
    queryClient.invalidateQueries(contestWebConfigQueryOptions(contestJid));
  },
});

export const upsertContestModuleConfigMutationOptions = contestJid => ({
  mutationFn: config => contestModuleAPI.upsertConfig(getToken(), contestJid, config),
  onSuccess: () => {
    queryClient.invalidateQueries(contestModuleConfigQueryOptions(contestJid));
  },
});
