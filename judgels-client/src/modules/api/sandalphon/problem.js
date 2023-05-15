export const ProblemType = {
  Programming: 'PROGRAMMING',
  Bundle: 'BUNDLE',
};

export function getProblemName(problem, language) {
  return problem.titlesByLanguage[language] || problem.titlesByLanguage[problem.defaultLanguage];
}

export function constructProblemName(title, alias) {
  return (alias ? alias + '. ' : '') + (title || '');
}
