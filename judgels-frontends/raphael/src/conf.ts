export enum Mode {
  PRIVATE_CONTESTS = 'PRIVATE_CONTESTS',
}

export interface ApiUrlsConfig {
  jophiel: string;
  uriel: string;
  jerahmeel?: string;
}

export interface WelcomeBannerConfig {
  title: string;
  description: string;
}

export interface GoogleAnalyticsConfig {
  trackingId: string;
}

export interface AppConfig {
  mode?: Mode;
  name: string;
  slogan: string;
  apiUrls: ApiUrlsConfig;
  welcomeBanner: WelcomeBannerConfig;
  googleAnalytics?: GoogleAnalyticsConfig;
}

export const APP_CONFIG = (window as any).conf as AppConfig;

export function hasJerahmeel() {
  return !!APP_CONFIG.apiUrls.jerahmeel;
}

export function isInPrivateContestsMode() {
  return APP_CONFIG.mode === Mode.PRIVATE_CONTESTS;
}
