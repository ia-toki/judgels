import { GradingEngineCode } from './engine';

export interface LanguageRestriction {
  allowedLanguageNames: string[];
}

export const gradingLanguageNamesMap = {
  C: 'C',
  Cpp11: 'C++11',
  Java: 'Java 8',
  Pascal: 'Pascal',
  Python3: 'Python 3',
  OutputOnly: '-',
};

export const gradingLanguageFilenameExtensionsMap = {
  C: ['c'],
  Cpp11: ['cc', 'cpp'],
  Java: ['java'],
  Pascal: ['pas'],
  Python3: ['py'],
  OutputOnly: ['zip'],
};

export const gradingLanguageSyntaxHighlighterValueMap = {
  C: 'c',
  Cpp11: 'cpp',
  Java: 'java',
  Pascal: 'pascal',
  Python3: 'python',
  OutputOnly: '',
};

export const preferredGradingLanguage = 'Cpp11';

export const gradingLanguages = Object.keys(gradingLanguageNamesMap).sort();

export function getGradingLanguageName(code: string): string {
  return gradingLanguageNamesMap[code] || code;
}

export function getGradingLanguageFilenameExtensions(code: string): string[] {
  return gradingLanguageFilenameExtensionsMap[code] || [];
}

export function getGradingLanguageSyntaxHighlighterValue(code: string): string {
  return gradingLanguageSyntaxHighlighterValueMap[code] || code;
}

export function getAllowedGradingLanguages(gradingEngine: string, restriction: LanguageRestriction) {
  if (gradingEngine === GradingEngineCode.OutputOnly) {
    return ['OutputOnly'];
  }
  if (restriction.allowedLanguageNames.length === 0) {
    return gradingLanguages;
  }
  return restriction.allowedLanguageNames;
}
