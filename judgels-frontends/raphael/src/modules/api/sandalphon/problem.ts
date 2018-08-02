import { LanguageRestriction } from 'modules/api/gabriel/language';

export interface ProblemInfo {
  slug: string;
  defaultLanguage: string;
  namesByLanguage: { [language: string]: string };
}

export function getProblemName(problem: ProblemInfo, language: string) {
  return problem.namesByLanguage[language] || problem.namesByLanguage[problem.defaultLanguage];
}

export function constructProblemName(name?: string, alias?: string) {
  return (alias && alias + '. ') + (name || '');
}

export interface ProblemStatement {
  name: string;
  timeLimit: number;
  memoryLimit: number;
  text: string;
}

export interface ProblemSubmissionConfig {
  sourceKeys: { [key: string]: string };
  gradingEngine: string;
  gradingLanguageRestriction: LanguageRestriction;
}

export interface ProblemWorksheet {
  statement: ProblemStatement;
  submissionConfig: ProblemSubmissionConfig;
  reasonNotAllowedToSubmit?: string;
}
