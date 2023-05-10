export const Mode = {
  JUDGELS: 'JUDGELS',
  TLX: 'TLX',
};

export const APP_CONFIG = window.conf;

export function isTLX() {
  return APP_CONFIG.mode === Mode.TLX;
}
