import { stringify } from 'query-string';

import { delete_, get, post } from 'modules/api/http';
import { Page } from 'modules/api/pagination';
import { ProfilesMap } from 'modules/api/jophiel/profile';

import { baseContestURL } from './contest';

export interface ContestContestant {
  userJid: string;
}

export interface ContestContestantConfig {
  canSupervise: boolean;
}

export interface ContestContestantsResponse {
  data: Page<ContestContestant>;
  profilesMap: ProfilesMap;
  config: ContestContestantConfig;
}

export interface ApprovedContestContestantsResponse {
  data: string[];
  profilesMap: ProfilesMap;
}

export interface ContestContestantUpsertResponse {
  insertedContestantProfilesMap: ProfilesMap;
  alreadyContestantProfilesMap: ProfilesMap;
}

export interface ContestContestantDeleteResponse {
  deletedContestantProfilesMap: ProfilesMap;
}

export enum ContestContestantState {
  None = 'NONE',
  Registrable = 'REGISTRABLE',
  Registrant = 'REGISTRANT',
  Contestant = 'CONTESTANT',
}

const baseURL = (contestJid: string) => `${baseContestURL(contestJid)}/contestants`;

export const contestContestantAPI = {
  getContestants: (token: string, contestJid: string, page?: number): Promise<ContestContestantsResponse> => {
    const params = stringify({ page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },

  getApprovedContestants: (token: string, contestJid: string): Promise<ApprovedContestContestantsResponse> => {
    return get(`${baseURL(contestJid)}/approved`, token);
  },

  getApprovedContestantsCount: (token: string, contestJid: string): Promise<number> => {
    return get(`${baseURL(contestJid)}/approved/count`, token);
  },

  registerMyselfAsContestant: (token: string, contestJid: string): Promise<void> => {
    return post(`${baseURL(contestJid)}/me`, token);
  },

  unregisterMyselfAsContestant: (token: string, contestJid: string): Promise<void> => {
    return delete_(`${baseURL(contestJid)}/me`, token);
  },

  getMyContestantState: (token: string, contestJid: string): Promise<ContestContestantState> => {
    return get(`${baseURL(contestJid)}/me/state`, token);
  },

  upsertContestants: (
    token: string,
    contestJid: string,
    usernames: string[]
  ): Promise<ContestContestantUpsertResponse> => {
    return post(`${baseURL(contestJid)}/batch-upsert`, token, usernames);
  },

  deleteContestants: (
    token: string,
    contestJid: string,
    usernames: string[]
  ): Promise<ContestContestantDeleteResponse> => {
    return post(`${baseURL(contestJid)}/batch-delete`, token, usernames);
  },
};
