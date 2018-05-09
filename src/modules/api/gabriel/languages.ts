const gradingLanguagesMap = {
  C: 'C',
  Cpp11: 'C++11',
  Pascal: 'Pascal',
  Python3: 'Python 3',
};

export function getGradingLanguageName(code: string): string {
  return gradingLanguagesMap[code] || code;
}
