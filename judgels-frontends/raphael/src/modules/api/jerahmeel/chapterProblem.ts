import { stringify } from 'query-string';

import { get } from '../http';
import { ProblemInfo, ProblemType } from '../sandalphon/problem';
import { baseChapterURL } from './chapter';

export interface ChapterProblem {
  alias: string;
  problemJid: string;
  type: ProblemType;
}

export interface ChapterProblemsResponse {
  data: ChapterProblem[];
  problemsMap: { [problemJid: string]: ProblemInfo };
}

export interface ChapterProblemWorksheet {
  defaultLanguage: string;
  languages: string[];
  problem: ChapterProblem;
}

const baseURL = (chapterJid: string) => `${baseChapterURL(chapterJid)}/problems`;

export const chapterProblemAPI = {
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
