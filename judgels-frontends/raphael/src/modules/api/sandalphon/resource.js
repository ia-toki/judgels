export interface ResourceInfo {
  slug: string;
  defaultLanguage: string;
  titlesByLanguage: { [language: string]: string };
}
