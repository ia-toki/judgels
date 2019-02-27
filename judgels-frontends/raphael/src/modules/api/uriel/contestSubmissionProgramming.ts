import { stringify } from 'query-string';

import { get, postMultipart, post } from 'modules/api/http';
import { Page } from 'modules/api/pagination';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { Submission, SubmissionWithSourceResponse } from 'modules/api/sandalphon/submissionProgramming';

import { baseContestsURL } from './contest';
import { ContestSubmissionConfig } from './contestSubmission';

export interface ContestSubmissionsResponse {
  data: Page<Submission>;
  config: ContestSubmissionConfig;
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
}

export interface ContestSubmissionRegradeAllData {
  contestJid?: string;
  userJid?: string;
  problemJid?: string;
}

const baseURL = `${baseContestsURL}/submissions/programming`;

export const contestSubmissionProgrammingAPI = {
  getSubmissions: (
    token: string,
    contestJid?: string,
    userJid?: string,
    problemJid?: string,
    page?: number
  ): Promise<ContestSubmissionsResponse> => {
    const params = stringify({ contestJid, userJid, problemJid, page });
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

  createSubmission: (
    token: string,
    contestJid: string,
    problemJid: string,
    gradingLanguage: string,
    sourceFiles: { [key: string]: File }
  ): Promise<void> => {
    const parts = { contestJid, problemJid, gradingLanguage, ...sourceFiles };
    return postMultipart(baseURL, token, parts);
  },

  regradeSubmissions: (token: string, submissionJids: string[]): Promise<void> => {
    return post(`${baseURL}/regrade`, token, submissionJids);
  },

  regradeAllSubmissions: (token: string, contestJid?: string, userJid?: string, problemJid?: string): Promise<void> => {
    return post(`${baseURL}/regrade/all`, token, {
      contestJid,
      userJid,
      problemJid,
    } as ContestSubmissionRegradeAllData);
  },
};
