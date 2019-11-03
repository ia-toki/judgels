import { stringify } from 'query-string';

import { get } from '../../../modules/api/http';
import { Page } from '../../../modules/api/pagination';
import { ProfilesMap } from '../../../modules/api/jophiel/profile';
import { Submission, SubmissionWithSourceResponse } from '../../../modules/api/sandalphon/submissionProgramming';

import { baseChaptersURL } from './chapter';

export interface ChapterSubmissionsResponse {
  data: Page<Submission>;
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
}

const baseURL = `${baseChaptersURL}/submissions/programming`;

export const chapterSubmissionProgrammingAPI = {
  getSubmissions: (
    token: string,
    chapterJid?: string,
    userJid?: string,
    problemJid?: string,
    page?: number
  ): Promise<ChapterSubmissionsResponse> => {
    const params = stringify({ chapterJid, userJid, problemJid, page });
    return get(`${baseURL}?${params}`, token);
  },

  getSubmissionWithSource: (
    token: string,
    submissionId: number,
    language?: string
  ): Promise<SubmissionWithSourceResponse> => {
    const params = stringify({ language });
    return get(`${baseURL}/id/${submissionId}?${params}`, token);
  },
};
