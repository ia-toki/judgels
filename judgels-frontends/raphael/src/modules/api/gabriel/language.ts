export interface LanguageRestriction {
  allowedLanguageNames: string[];
}

export const OutputOnlyOverrides = {
  KEY: 'OutputOnly',
  NAME: '-',
};

export const gradingLanguageNamesMap = {
  C: 'C',
  Cpp11: 'C++11',
  Java: 'Java 8',
  Pascal: 'Pascal',
  Python3: 'Python 3',
  OutputOnly: '-',
};

export const gradingLanguageFamiliesMap = {
  C: 'C',
  Cpp11: 'C++',
  Java: 'Java',
  Pascal: 'Pascal',
  Python3: 'Python',
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

export const gradingLanguages = Object.keys(gradingLanguageNamesMap)
  .filter(l => l !== OutputOnlyOverrides.KEY)
  .sort();

export function getGradingLanguageName(code: string): string {
  return gradingLanguageNamesMap[code] || code;
}

export function getGradingLanguageFamily(code: string): string {
  return gradingLanguageFamiliesMap[code];
}

export function getGradingLanguageFilenameExtensions(code: string): string[] {
  return gradingLanguageFilenameExtensionsMap[code] || [];
}

export function getGradingLanguageSyntaxHighlighterValue(code: string): string {
  return gradingLanguageSyntaxHighlighterValueMap[code] || code;
}

export function getAllowedGradingLanguages(gradingEngine: string, restriction: LanguageRestriction) {
  if (gradingEngine.startsWith(OutputOnlyOverrides.KEY)) {
    return [OutputOnlyOverrides.KEY];
  }
  if (restriction.allowedLanguageNames.length === 0) {
    return gradingLanguages;
  }
  return restriction.allowedLanguageNames;
}

export function allLanguagesAllowed(r: LanguageRestriction) {
  return !r.allowedLanguageNames || r.allowedLanguageNames.length === 0;
}
