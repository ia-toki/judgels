import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get } from '../http';
import { Profile } from '../jophiel/profile';

export interface UserStats {
  totalScores: number;
  totalProblemsTried: number;
  totalProblemVerdictsMap: { [verdict: string]: number };
}

export interface UserTopStatsEntry {
  userJid: string;
  totalScores: number;
}

export interface UserTopStats {
  topUsers: UserTopStatsEntry[];
}

export interface UserTopStatsResponse {
  data: UserTopStats;
  profilesMap: { [userJid: string]: Profile };
}

export const baseUserStatsURL = `${APP_CONFIG.apiUrls.jerahmeel}/user-stats`;

export const userStatsAPI = {
  getUserStats: (username: string): Promise<UserStats> => {
    const params = stringify({ username });
    return get(`${baseUserStatsURL}?${params}`);
  },

  getTopUserStats: (page?: number, pageSize?: number): Promise<UserTopStatsResponse> => {
    const params = stringify({ page, pageSize });
    return get(`${baseUserStatsURL}/top?${params}`);
  },
};
