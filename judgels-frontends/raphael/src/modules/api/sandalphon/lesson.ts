export interface LessonInfo {
  slug: string;
  defaultLanguage: string;
  titlesByLanguage: { [language: string]: string };
}

export function getLessonName(lesson: LessonInfo, language: string) {
  return lesson.titlesByLanguage[language] || lesson.titlesByLanguage[lesson.defaultLanguage];
}

export function constructLessonName(title?: string, alias?: string) {
  return (alias && alias + '. ') + (title || '');
}
