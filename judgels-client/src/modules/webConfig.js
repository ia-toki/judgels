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

export function isTrainingAccountRegistrationEnabled() {
  return !!getUserWebConfig()?.trainingAccountRegistrationEnabled;
}

export function isTrainingAccountPasswordResetEnabled() {
  return !!getUserWebConfig()?.trainingAccountPasswordResetEnabled;
}

export function isTrainingEnabled() {
  return !!getUserWebConfig()?.trainingEnabled;
}

export function isRatingEnabled() {
  return !!getUserWebConfig()?.ratingEnabled;
}

export function getGoogleAuthClientId() {
  return getUserWebConfig()?.googleAuthClientId;
}

export function getRecaptchaSiteKey() {
  return getUserWebConfig()?.recaptchaSiteKey;
}

export function getGoogleAnalyticsTrackingId() {
  return getUserWebConfig()?.googleAnalyticsTrackingId;
}
