export enum Mode {
  PRIVATE_CONTESTS = 'PRIVATE_CONTESTS',
}

export interface ApiUrlsConfig {
  jophiel: string;
  legacyJophiel: string;
  uriel: string;
  jerahmeel: string;
}

export interface TempHomeConfig {
  jerahmeelUrl: string;
}

export interface WelcomeBannerConfig {
  title: string;
  description: string;
}

export interface AppConfig {
  mode?: Mode;
  name: string;
  slogan: string;
  apiUrls: ApiUrlsConfig;
  tempHome: TempHomeConfig;
  welcomeBanner: WelcomeBannerConfig;
}

export const APP_CONFIG = (window as any).conf as AppConfig;
