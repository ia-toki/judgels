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

export function getHomeBanner() {
  return getUserWebConfig()?.homeBanner;
}

export function isUserRegistrationEnabled() {
  return !!getUserWebConfig()?.userRegistrationEnabled;
}

export function isUserResetPasswordEnabled() {
  return !!getUserWebConfig()?.userResetPasswordEnabled;
}

export function isTrainingEnabled() {
  return !!getUserWebConfig()?.trainingEnabled;
}

export function isRatingEnabled() {
  return !!getUserWebConfig()?.ratingEnabled;
}
