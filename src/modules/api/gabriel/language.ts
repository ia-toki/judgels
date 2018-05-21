export interface LanguageRestriction {
  allowedLanguageNames: string[];
}

export const gradingLanguageNamesMap = {
  C: 'C',
  Cpp11: 'C++11',
  Java: 'Java 8',
  Pascal: 'Pascal',
  Python3: 'Python 3',
};

export const preferredGradingLanguage = 'Cpp11';

export const gradingLanguages = Object.keys(gradingLanguageNamesMap).sort();

export function getGradingLanguageName(code: string): string {
  return gradingLanguageNamesMap[code] || code;
}

export function getAllowedGradingLanguages(restriction: LanguageRestriction) {
  if (restriction.allowedLanguageNames.length === 0) {
    return gradingLanguages;
  }
  return restriction.allowedLanguageNames;
}
