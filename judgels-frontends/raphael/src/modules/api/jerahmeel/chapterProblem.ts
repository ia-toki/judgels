import { get } from '../http';
import { ProblemInfo } from '../sandalphon/problem';
import { baseChapterURL } from './chapter';

export interface ChapterProblem {
  alias: string;
  problemJid: string;
}

export interface ChapterProblemsResponse {
  data: ChapterProblem[];
  problemsMap: { [problemJid: string]: ProblemInfo };
}

const baseURL = (chapterJid: string) => `${baseChapterURL(chapterJid)}/problems`;

export const chapterProblemAPI = {
  getProblems: (token: string, chapterJid: string): Promise<ChapterProblemsResponse> => {
    return get(baseURL(chapterJid), token);
  },
};
