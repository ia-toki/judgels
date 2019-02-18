import { LanguageRestriction } from 'modules/api/gabriel/language';

export enum ProblemType {
  Programming = 'PROGRAMMING',
  Bundle = 'BUNDLE'
}

export interface ProblemInfo {
  slug: string;
  type: ProblemType;
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

export interface ProgrammingProblemWorksheet {
  statement: ProblemStatement;
  limits: ProblemLimits;
  submissionConfig: ProblemSubmissionConfig;
  reasonNotAllowedToSubmit?: string;
}

export enum BundleItemType {
  Statement = 'STATEMENT',
  MultipleChoice = 'MULTIPLE_CHOICE'
}

export interface BundleItem {
  jid: string;
  type: BundleItemType;
  meta: string;
  config: string;
}

export interface BundleProblemWorksheet {
  reasonNotAllowedToSubmit?: string;
  items: BundleItem[];
}

export type ProblemWorksheet = ProgrammingProblemWorksheet | BundleProblemWorksheet
