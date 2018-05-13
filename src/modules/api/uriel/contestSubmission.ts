import { APP_CONFIG } from '../../../conf';
import { get } from '../http';
import { Page } from '../pagination';
import { UsersMap } from '../jophiel/user';
import { Submission, SubmissionWithSourceResponse } from '../sandalphon/submission';

export interface ContestSubmissionsResponse {
  data: Page<Submission>;
  usersMap: UsersMap;
  problemAliasesMap: { [problemJid: string]: string };
}

export function createContestSubmissionAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/submissions/contest`;

  return {
    getMySubmissions: (token: string, contestJid: string, page: number): Promise<ContestSubmissionsResponse> => {
      return get(`${baseURL}/mine?contestJid=${contestJid}&page=${page}`, token);
    },

    getSubmissionWithSource: (token: string, submissionId: number): Promise<SubmissionWithSourceResponse> => {
      return get(`${baseURL}/id/${submissionId}`, token);
    },
  };
}
