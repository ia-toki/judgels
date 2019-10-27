import { ResourceInfo } from './resource';

export enum ProblemType {
  Programming = 'PROGRAMMING',
  Bundle = 'BUNDLE',
}

export interface ProblemInfo extends ResourceInfo {
  type: ProblemType;
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
