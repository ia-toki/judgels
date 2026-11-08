export const APP_CONFIG = window.conf;

export function isTrainingEnabled() {
  return !!APP_CONFIG.training?.enabled;
}

export function isRatingEnabled() {
  return isTrainingEnabled() && !!APP_CONFIG.training?.rating?.enabled;
}
