import { APP_CONFIG } from 'conf';
import { get, postMultipart } from 'modules/api/http';
import { Page } from 'modules/api/pagination';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { Submission, SubmissionWithSourceResponse } from 'modules/api/sandalphon/submission';

export interface ContestSubmissionsResponse {
  data: Page<Submission>;
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
}

export interface ContestSubmissionConfig {
  isAllowedToViewAllSubmissions: boolean;
}

export function createContestSubmissionAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests/submissions`;

  return {
    getSubmissions: (token: string, contestJid: string, page: number): Promise<ContestSubmissionsResponse> => {
      return get(`${baseURL}?contestJid=${contestJid}&page=${page}`, token);
    },

    getSubmissionConfig: (token: string, contestJid: string): Promise<ContestSubmissionConfig> => {
      return get(`${baseURL}/config?contestJid=${contestJid}`, token);
    },

    getSubmissionWithSource: (
      token: string,
      submissionId: number,
      language: string
    ): Promise<SubmissionWithSourceResponse> => {
      const languageParam = language ? `?language=${language}` : '';
      return get(`${baseURL}/id/${submissionId}${languageParam}`, token);
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
