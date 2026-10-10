import { stringify } from 'query-string';

import { APP_CONFIG } from '../../conf';
import { get, post } from './http';

export const ProblemType = {
  Programming: 'PROGRAMMING',
  Bundle: 'BUNDLE',
};

export const problemTypeNamesMap = {
  [ProblemType.Programming]: 'Programming',
  [ProblemType.Bundle]: 'Bundle',
};

export const ProblemSetterRole = {
  Writer: 'WRITER',
  Developer: 'DEVELOPER',
  Tester: 'TESTER',
  Editorialist: 'EDITORIALIST',
};

export const ProblemErrors = {
  SlugAlreadyExists: 'ProblemSlugAlreadyExists',
  SetterUsernamesNotFound: 'ProblemSetterUsernamesNotFound',
  VersionLocalChangesOutdated: 'ProblemVersionLocalChangesOutdated',
  VersionLocalChangesConflict: 'ProblemVersionLocalChangesConflict',
};

export function getProblemName(problem, language) {
  return (language && problem.titlesByLanguage[language]) || problem.titlesByLanguage[problem.defaultLanguage];
}

export function constructProblemName(title, alias) {
  return (alias ? alias + '. ' : '') + (title || '');
}

export const baseProblemsURL = `${APP_CONFIG.apiUrl}/v4/problems`;

export function baseProblemURL(problemJid) {
  return `${baseProblemsURL}/${problemJid}`;
}

export const problemAPI = {
  createProblem: (token, data) => {
    return post(baseProblemsURL, token, data);
  },

  getProblem: (token, problemJid) => {
    return get(baseProblemURL(problemJid), token);
  },

  getProblemBySlug: (token, problemSlug) => {
    return get(`${baseProblemsURL}/slug/${problemSlug}`, token);
  },

  updateProblem: (token, problemJid, data) => {
    return post(baseProblemURL(problemJid), token, data);
  },

  getProblems: (token, term, tags, page) => {
    const params = stringify({ term, tags, page });
    return get(`${baseProblemsURL}?${params}`, token);
  },
};
