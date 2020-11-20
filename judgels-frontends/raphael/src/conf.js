export const Mode = {
  PRIVATE_CONTESTS: 'PRIVATE_CONTESTS',
};

export const APP_CONFIG = window.conf;

export function hasJerahmeel() {
  return !!APP_CONFIG.apiUrls.jerahmeel;
}

export function isInPrivateContestsMode() {
  return APP_CONFIG.mode === Mode.PRIVATE_CONTESTS;
}
