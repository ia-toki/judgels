export interface ApiUrlsConfig {
  jophiel: string;
  legacyJophiel: string;
  uriel: string;
}

export interface TempHomeConfig {
  jerahmeelUrl: string;
}

export interface WelcomeBannerConfig {
  title: string;
  description: string;
}

export interface AppConfig {
  name: string;
  slogan: string;
  apiUrls: ApiUrlsConfig;
  tempHome: TempHomeConfig;
  welcomeBanner: WelcomeBannerConfig;
}

export const APP_CONFIG = (window as any).conf as AppConfig;
