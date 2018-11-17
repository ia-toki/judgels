import { stringify } from 'query-string';

import { APP_CONFIG } from 'conf';
import { delete_, get, post } from 'modules/api/http';
import { Page } from 'modules/api/pagination';
import { ProfilesMap } from 'modules/api/jophiel/profile';

export interface ContestContestant {
  userJid: string;
}

export interface ContestContestantsResponse {
  data: Page<ContestContestant>;
  profilesMap: ProfilesMap;
}

export interface ApprovedContestContestantsResponse {
  data: string[];
  profilesMap: ProfilesMap;
}

export interface ContestContestantUpsertResponse {
  insertedContestantProfilesMap: ProfilesMap;
  alreadyContestantProfilesMap: ProfilesMap;
}

export enum ContestContestantState {
  None = 'NONE',
  Registrable = 'REGISTRABLE',
  Registrant = 'REGISTRANT',
  Contestant = 'CONTESTANT',
}

export function createContestContestantAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getContestants: (token: string, contestJid: string, page?: number): Promise<ContestContestantsResponse> => {
      const params = stringify({ page });
      return get(`${baseURL}/${contestJid}/contestants?${params}`, token);
    },

    getApprovedContestants: (token: string, contestJid: string): Promise<ApprovedContestContestantsResponse> => {
      return get(`${baseURL}/${contestJid}/contestants/approved`, token);
    },

    getApprovedContestantsCount: (token: string, contestJid: string): Promise<number> => {
      return get(`${baseURL}/${contestJid}/contestants/approved/count`, token);
    },

    registerMyselfAsContestant: (token: string, contestJid: string): Promise<void> => {
      return post(`${baseURL}/${contestJid}/contestants/me`, token);
    },

    unregisterMyselfAsContestant: (token: string, contestJid: string): Promise<void> => {
      return delete_(`${baseURL}/${contestJid}/contestants/me`, token);
    },

    getMyContestantState: (token: string, contestJid: string): Promise<ContestContestantState> => {
      return get(`${baseURL}/${contestJid}/contestants/me/state`, token);
    },

    upsertContestants: (
      token: string,
      contestJid: string,
      usernames: string[]
    ): Promise<ContestContestantUpsertResponse> => {
      return post(`${baseURL}/${contestJid}/contestants`, token, usernames);
    },
  };
}
