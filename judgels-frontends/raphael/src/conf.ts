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

export interface TermsAndConditionsConfig {
  contest: string;
}

export interface AppConfig {
  name: string;
  slogan: string;
  apiUrls: ApiUrlsConfig;
  tempHome: TempHomeConfig;
  welcomeBanner: WelcomeBannerConfig;
  termsAndConditions: TermsAndConditionsConfig;
}

export const APP_CONFIG = (window as any).conf as AppConfig;
