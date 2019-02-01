import { LanguageRestriction } from 'modules/api/gabriel/language';

export interface ProblemInfo {
  slug: string;
  defaultLanguage: string;
  titlesByLanguage: { [language: string]: string };
}

export function getProblemName(problem: ProblemInfo, language: string) {
  return problem.titlesByLanguage[language] || problem.titlesByLanguage[problem.defaultLanguage];
}

export function constructProblemName(title?: string, alias?: string) {
  return (alias && alias + '. ') + (title || '');
}

export interface ProblemStatement {
  title: string;
  text: string;
}

export interface ProblemLimits {
  timeLimit: number;
  memoryLimit: number;
}

export interface ProblemSubmissionConfig {
  sourceKeys: { [key: string]: string };
  gradingEngine: string;
  gradingLanguageRestriction: LanguageRestriction;
}

export interface ProblemWorksheet {
  statement: ProblemStatement;
  limits: ProblemLimits;
  submissionConfig: ProblemSubmissionConfig;
  reasonNotAllowedToSubmit?: string;
}
