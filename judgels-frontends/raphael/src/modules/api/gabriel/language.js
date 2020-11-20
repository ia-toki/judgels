export const OutputOnlyOverrides = {
  KEY: 'OutputOnly',
  NAME: '-',
};

export const gradingLanguageNamesMap = {
  C: 'C',
  Cpp11: 'C++11',
  Cpp17: 'C++17',
  Go: 'Go',
  Java: 'Java 8',
  Pascal: 'Pascal',
  Python3: 'Python 3',
  PyPy3: 'PyPy 3',
  OutputOnly: '-',
};

export const gradingLanguageFamiliesMap = {
  C: 'C',
  Cpp11: 'C++',
  Cpp17: 'C++',
  Go: 'Go',
  Java: 'Java',
  Pascal: 'Pascal',
  Python3: 'Python',
  PyPy3: 'Python',
};

export const gradingLanguageFilenameExtensionsMap = {
  C: ['c'],
  Cpp11: ['cc', 'cpp'],
  Cpp17: ['cc', 'cpp'],
  Go: ['go'],
  Java: ['java'],
  Pascal: ['pas'],
  Python3: ['py'],
  PyPy3: ['py'],
  OutputOnly: ['zip'],
};

export const gradingLanguageSyntaxHighlighterValueMap = {
  C: 'c',
  Cpp11: 'cpp',
  Cpp17: 'cpp',
  Go: 'go',
  Java: 'java',
  Pascal: 'pascal',
  Python3: 'python',
  PyPy3: 'python',
  OutputOnly: '',
};

export const gradingLanguages = Object.keys(gradingLanguageNamesMap)
  .filter(l => l !== OutputOnlyOverrides.KEY)
  .sort();

export function getGradingLanguageName(code) {
  return gradingLanguageNamesMap[code] || code;
}

export function getGradingLanguageFamily(code) {
  return gradingLanguageFamiliesMap[code];
}

export function getGradingLanguageFilenameExtensions(code) {
  return gradingLanguageFilenameExtensionsMap[code] || [];
}

export function getGradingLanguageSyntaxHighlighterValue(code) {
  return gradingLanguageSyntaxHighlighterValueMap[code] || code;
}

export function getAllowedGradingLanguages(gradingEngine, restriction) {
  if (gradingEngine.startsWith(OutputOnlyOverrides.KEY)) {
    return [OutputOnlyOverrides.KEY];
  }
  if (restriction.allowedLanguageNames.length === 0) {
    return gradingLanguages;
  }
  return restriction.allowedLanguageNames;
}

export function allLanguagesAllowed(r) {
  return !r.allowedLanguageNames || r.allowedLanguageNames.length === 0;
}
