import { userWebConfigQueryOptions } from './queries/userWeb';
import { queryClient } from './queryClient';

function getUserWebConfig() {
  return queryClient.getQueryData(userWebConfigQueryOptions().queryKey);
}

export function getAppName() {
  return getUserWebConfig()?.appName || 'Judgels';
}

export function getAppSlogan() {
  return getUserWebConfig()?.appSlogan || 'Programming Contest System';
}
