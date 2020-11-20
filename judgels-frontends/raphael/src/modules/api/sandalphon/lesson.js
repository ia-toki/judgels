export function getLessonName(lesson, language) {
  return lesson.titlesByLanguage[language] || lesson.titlesByLanguage[lesson.defaultLanguage];
}

export function constructLessonName(title, alias) {
  return (alias && alias + '. ') + (title || '');
}
