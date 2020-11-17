import { stringify } from 'query-string';

import { get, put } from '../http';
import { ProblemInfo, ProblemType } from '../sandalphon/problem';
import { ProblemProgress } from './problem';
import { baseChapterURL } from './chapter';

export interface ChapterProblem {
  alias: string;
  problemJid: string;
  type: ProblemType;
}

export interface ChapterProblemData {
  alias: string;
  slug: string;
  type: ProblemType;
}

export interface ChapterProblemsResponse {
  data: ChapterProblem[];
  problemsMap: { [problemJid: string]: ProblemInfo };
  problemProgressesMap: { [problemJid: string]: ProblemProgress };
}

export interface ChapterProblemWorksheet {
  defaultLanguage: string;
  languages: string[];
  problem: ChapterProblem;
}

const baseURL = (chapterJid: string) => `${baseChapterURL(chapterJid)}/problems`;

export const chapterProblemAPI = {
  setProblems: (token: string, chapterJid: string, data: ChapterProblemData[]): Promise<void> => {
    return put(baseURL(chapterJid), token, data);
  },

  getProblems: (token: string, chapterJid: string): Promise<ChapterProblemsResponse> => {
    return get(baseURL(chapterJid), token);
  },

  getProblemWorksheet: (
    token: string,
    chapterJid: string,
    problemAlias: string,
    language?: string
  ): Promise<ChapterProblemWorksheet> => {
    const params = stringify({ language });
    return get(`${baseURL(chapterJid)}/${problemAlias}/worksheet?${params}`, token);
  },
};
