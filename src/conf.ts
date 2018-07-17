export interface ApiUrlsConfig {
  jophiel: string;
  legacyJophiel: string;
}

export interface TempHomeConfig {
  urielUrl: string;
  jerahmeelUrl: string;
}

export interface AppConfig {
  name: string;
  slogan: string;
  apiUrls: ApiUrlsConfig;
  tempHome: TempHomeConfig;
}

export const APP_CONFIG = (window as any).conf as AppConfig;
