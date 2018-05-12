export enum GradingLanguageCode {
  OutputOnly = '-',
}

const gradingLanguageNamesMap = {
  Cpp11: 'C++11',
  Python3: 'Python 3',
};

export function getGradingLanguageName(code: string): string {
  return gradingLanguageNamesMap[code] || code;
}
