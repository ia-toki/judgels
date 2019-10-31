import { ResourceInfo } from './resource';

export interface LessonInfo extends ResourceInfo {}

export interface LessonStatement {
  title: string;
  text: string;
}

export function getLessonName(lesson: LessonInfo, language: string) {
  return lesson.titlesByLanguage[language] || lesson.titlesByLanguage[lesson.defaultLanguage];
}

export function constructLessonName(title?: string, alias?: string) {
  return (alias && alias + '. ') + (title || '');
}
