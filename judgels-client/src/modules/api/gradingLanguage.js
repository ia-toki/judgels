export const OutputOnlyOverrides = {
  KEY: 'OutputOnly',
  NAME: '-',
};

export const gradingLanguageNamesMap = {
  C: 'C',
  Cpp11: 'C++11',
  Cpp17: 'C++17',
  Cpp20: 'C++20',
  Go: 'Go',
  Java: 'Java 17',
  Pascal: 'Pascal',
  Python3: 'Python 3',
  PyPy3: 'PyPy 3',
  Rust2021: 'Rust 2021',
  OutputOnly: '-',
};

export const gradingLanguageFamiliesMap = {
  C: 'C',
  Cpp11: 'C++',
  Cpp17: 'C++',
  Cpp20: 'C++',
  Go: 'Go',
  Java: 'Java',
  Pascal: 'Pascal',
  Python3: 'Python',
  PyPy3: 'Python',
  Rust2021: 'Rust',
};

export const gradingLanguageFilenameExtensionsMap = {
  C: ['c'],
  Cpp11: ['cc', 'cpp'],
  Cpp17: ['cc', 'cpp'],
  Cpp20: ['cc', 'cpp'],
  Go: ['go'],
  Java: ['java'],
  Pascal: ['pas'],
  Python3: ['py'],
  PyPy3: ['py'],
  Rust2021: ['rs'],
  OutputOnly: ['zip'],
};

export const gradingLanguageSyntaxHighlighterValueMap = {
  C: 'c',
  Cpp11: 'cpp',
  Cpp17: 'cpp',
  Cpp20: 'cpp',
  Go: 'go',
  Java: 'java',
  Pascal: 'pascal',
  Python3: 'python',
  PyPy3: 'python',
  Rust2021: 'rust',
  OutputOnly: '',
};

export const gradingLanguageEditorSubmissionFilenamesMap = {
  C: 'solution.c',
  Cpp11: 'solution.cpp',
  Cpp17: 'solution.cpp',
  Cpp20: 'solution.cpp',
  Go: 'solution.go',
  Java: 'Solution.java',
  Pascal: 'solution.pas',
  Python3: 'solution.py',
  PyPy3: 'solution.py',
  Rust2021: 'solution.rs',
};

export const gradingLanguageEditorSubmissionHintsMap = {
  Java: 'Public class name must be Solution',
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

export function getGradingLanguageEditorSubmissionFilename(code) {
  return gradingLanguageEditorSubmissionFilenamesMap[code];
}

export function getGradingLanguageEditorSubmissionHint(code) {
  return gradingLanguageEditorSubmissionHintsMap[code];
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
