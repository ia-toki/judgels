import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get } from '../../../modules/api/http';
import { UserRating } from '../jophiel/userRating';
import { ContestInfo } from './contest';

export interface ContestRating {
  contestJid: string;
  rating: UserRating;
}

export interface ContestRatingHistoryResponse {
  data: ContestRating[];
  contestsMap: { [key: string]: ContestInfo };
}

const baseURL = `${APP_CONFIG.apiUrls.uriel}/contest-rating`;

export const contestRatingAPI = {
  getRatingHistory: (username: string): Promise<ContestRatingHistoryResponse> => {
    const params = stringify({ username });
    return get(`${baseURL}/history?${params}`);
  },
};
