import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, postMultipart, post } from '../http';
import { Page } from '../pagination';
import { ProfilesMap } from '../jophiel/profile';
import { Submission } from '../sandalphon/submissionProgramming';
import { SubmissionWithSourceResponse } from '../sandalphon/submissionProgramming';
import { SubmissionConfig } from './submission';

export const baseSubmissionsURL = `${APP_CONFIG.apiUrls.jerahmeel}/submissions/programming`;

export interface SubmissionsResponse {
  data: Page<Submission>;
  config: SubmissionConfig;
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
  problemNamesMap: { [problemJid: string]: string };
  containerNamesMap: { [problemJid: string]: string };
  containerPathsMap: { [problemJid: string]: string[] };
}

export const submissionProgrammingAPI = {
  getSubmissions: (
    token: string,
    containerJid?: string,
    username?: string,
    problemJid?: string,
    problemAlias?: string,
    page?: number
  ): Promise<SubmissionsResponse> => {
    const params = stringify({ containerJid, username, problemJid, problemAlias, page });
    return get(`${baseSubmissionsURL}?${params}`, token);
  },

  getSubmissionWithSource: (
    token: string,
    submissionId: number,
    language?: string
  ): Promise<SubmissionWithSourceResponse> => {
    const params = stringify({ language });
    return get(`${baseSubmissionsURL}/id/${submissionId}?${params}`, token);
  },

  createSubmission: (
    token: string,
    containerJid: string,
    problemJid: string,
    gradingLanguage: string,
    sourceFiles: { [key: string]: File }
  ): Promise<void> => {
    const parts = { containerJid, problemJid, gradingLanguage, ...sourceFiles };
    return postMultipart(baseSubmissionsURL, token, parts);
  },

  regradeSubmission: (token: string, submissionJid: string): Promise<void> => {
    return post(`${baseSubmissionsURL}/${submissionJid}/regrade`, token);
  },

  regradeSubmissions: (
    token: string,
    containerJid?: string,
    username?: string,
    problemJid?: string,
    problemAlias?: string
  ): Promise<void> => {
    const params = stringify({ containerJid, username, problemJid, problemAlias });
    return post(`${baseSubmissionsURL}/regrade?${params}`, token);
  },
};
