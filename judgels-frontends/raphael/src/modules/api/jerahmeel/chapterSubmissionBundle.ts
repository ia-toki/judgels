import { stringify } from 'query-string';

import { get, post } from '../../../modules/api/http';
import { Page } from '../../../modules/api/pagination';
import { ItemSubmission } from '../../../modules/api/sandalphon/submissionBundle';
import { ChapterSubmissionConfig } from './chapterSubmission';
import { Profile } from '../../../modules/api/jophiel/profile';
import { baseChaptersURL } from './chapter';
import { ItemType } from '../sandalphon/problemBundle';

export interface ChapterItemSubmissionData {
  chapterJid: string;
  problemJid: string;
  itemJid: string;
  answer: string;
}

export interface ChapterItemSubmissionsResponse {
  data: Page<ItemSubmission>;
  config: ChapterSubmissionConfig;
  profilesMap: { [id: string]: Profile };
  problemAliasesMap: { [id: string]: string };
  itemNumbersMap: { [itemJid: string]: number };
  itemTypesMap: { [itemJid: string]: ItemType };
}

export interface ChapterantAnswerSummaryResponse {
  profile: Profile;
  config: ChapterSubmissionConfig;
  itemJidsByProblemJid: { [problemJid: string]: string[] };
  submissionsByItemJid: { [itemJid: string]: ItemSubmission };
  problemAliasesMap: { [id: string]: string };
  problemNamesMap: { [id: string]: string };
  itemTypesMap: { [itemJid: string]: ItemType };
}

const baseURL = `${baseChaptersURL}/submissions/bundle`;

export const chapterSubmissionBundleAPI = {
  getSubmissions: (
    token: string,
    chapterJid: string,
    username?: string,
    problemAlias?: string,
    page?: number
  ): Promise<ChapterItemSubmissionsResponse> => {
    const params = stringify({ chapterJid, username, problemAlias, page });
    return get(`${baseURL}?${params}`, token);
  },

  createItemSubmission: (token: string, data: ChapterItemSubmissionData): Promise<void> => {
    return post(`${baseURL}/`, token, data);
  },

  getAnswerSummary: (
    token: string,
    chapterJid: string,
    username?: string,
    language?: string
  ): Promise<ChapterantAnswerSummaryResponse> => {
    const params = stringify({ chapterJid, username, language });
    return get(`${baseURL}/summary?${params}`, token);
  },

  getLatestSubmissionsByUserForProblemInChapter: (
    token: string,
    chapterJid: string,
    problemAlias: string,
    username?: string
  ): Promise<{ [id: string]: ItemSubmission }> => {
    const params = stringify({ chapterJid, username, problemAlias });
    return get(`${baseURL}/answers?${params}`, token);
  },

  regradeSubmission: (token: string, submissionJid: string): Promise<void> => {
    return post(`${baseURL}/${submissionJid}/regrade`, token);
  },

  regradeSubmissions: (token: string, chapterJid?: string, userJid?: string, problemJid?: string): Promise<void> => {
    const params = stringify({ chapterJid, userJid, problemJid });
    return post(`${baseURL}/regrade?${params}`, token);
  },
};
