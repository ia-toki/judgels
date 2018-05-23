import { APP_CONFIG } from '../../../conf';
import { get, postMultipart } from '../http';
import { Page } from '../pagination';
import { UsersMap } from '../jophiel/user';
import { Submission, SubmissionWithSourceResponse } from '../sandalphon/submission';

export interface ContestSubmissionsResponse {
  data: Page<Submission>;
  usersMap: UsersMap;
  problemAliasesMap: { [problemJid: string]: string };
}

export function createContestSubmissionAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests/submissions`;

  return {
    getMySubmissions: (token: string, contestJid: string, page: number): Promise<ContestSubmissionsResponse> => {
      return get(`${baseURL}/mine?contestJid=${contestJid}&page=${page}`, token);
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
