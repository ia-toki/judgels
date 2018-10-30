import { stringify } from 'query-string';

import { APP_CONFIG } from 'conf';
import { get, postMultipart } from 'modules/api/http';
import { Page } from 'modules/api/pagination';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { Submission, SubmissionWithSourceResponse } from 'modules/api/sandalphon/submission';

export interface ContestSubmissionsResponse {
  data: Page<Submission>;
  config: ContestSubmissionConfig;
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
}

export interface ContestSubmissionConfig {
  canSupervise: boolean;
  userJids: string[];
  problemJids: string[];
}

export function createContestSubmissionAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests/submissions`;

  return {
    getSubmissions: (
      token: string,
      contestJid: string,
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
      language: string
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
  };
}
