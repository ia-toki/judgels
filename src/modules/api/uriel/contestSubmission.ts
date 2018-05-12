import { APP_CONFIG } from '../../../conf';
import { get } from '../http';
import { Page } from '../pagination';
import { UserInfo, UsersMap } from '../jophiel/user';
import { Submission } from '../sandalphon/submission';
import { SubmissionSource } from '../gabriel/submission';

export interface ContestSubmissionsResponse {
  data: Page<Submission>;
  usersMap: UsersMap;
  problemAliasesMap: { [problemJid: string]: string };
}

export interface ContestSubmission {
  submission: Submission;
  source: SubmissionSource;
}

export interface ContestSubmissionResponse {
  data: ContestSubmission;
  user: UserInfo;
  problemName: string;
  problemAlias: string;
  contestName: string;
}

export function createContestSubmissionAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/submissions`;

  return {
    getMySubmissions: (token: string, contestJid: string, page: number): Promise<ContestSubmissionsResponse> => {
      return get(`${baseURL}/mine?contestJid=${contestJid}&page=${page}`, token);
    },

    getSubmission: (token: string, submissionId: number): Promise<ContestSubmissionResponse> => {
      return get(`${baseURL}/id/${submissionId}`, token);
    },
  };
}
