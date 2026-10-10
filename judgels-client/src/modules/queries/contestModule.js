import { queryOptions } from '@tanstack/react-query';

import { contestModuleAPI } from '../api/contestModule';
import { queryClient } from '../queryClient';
import { getToken } from '../session';
import { contestWebConfigQueryOptions } from './contestWeb';

export const contestModulesQueryOptions = contestJid =>
  queryOptions({
    queryKey: ['contest', contestJid, 'modules'],
    queryFn: () => contestModuleAPI.getModules(getToken(), contestJid),
  });

export const contestModulesConfigQueryOptions = contestJid =>
  queryOptions({
    queryKey: ['contest', contestJid, 'modules', 'config'],
    queryFn: () => contestModuleAPI.getModulesConfig(getToken(), contestJid),
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

export const upsertContestModulesConfigMutationOptions = contestJid => ({
  mutationFn: config => contestModuleAPI.upsertModulesConfig(getToken(), contestJid, config),
  onSuccess: () => {
    queryClient.invalidateQueries(contestModulesConfigQueryOptions(contestJid));
    queryClient.invalidateQueries(contestWebConfigQueryOptions(contestJid));
  },
});
