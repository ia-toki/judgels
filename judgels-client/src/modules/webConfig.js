import { userWebConfigQueryOptions } from './queries/userWeb';
import { queryClient } from './queryClient';

function getUserWebConfig() {
  return queryClient.getQueryData(userWebConfigQueryOptions().queryKey);
}

export function getAppName() {
  return getUserWebConfig()?.appName;
}

export function getAppSlogan() {
  return getUserWebConfig()?.appSlogan;
}
