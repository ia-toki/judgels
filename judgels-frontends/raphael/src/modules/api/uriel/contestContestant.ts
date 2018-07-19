import { UsersMap } from '../jophiel/user';
import { APP_CONFIG } from '../../../conf';
import { delete_, get, post } from '../http';

export interface ContestContestantsResponse {
  data: string[];
  usersMap: UsersMap;
  userCountriesMap: { [userJid: string]: string };
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
    getContestants: (token: string, contestJid: string): Promise<ContestContestantsResponse> => {
      return get(`${baseURL}/${contestJid}/contestants`, token);
    },

    getContestantsCount: (token: string, contestJid: string): Promise<number> => {
      return get(`${baseURL}/${contestJid}/contestants/count`, token);
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

    addContestants: (token: string, contestJid: string, userJids: string[]): Promise<void> => {
      return post(`${baseURL}/${contestJid}/contestants`, token, userJids);
    },
  };
}
