import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, post, put } from '../../../modules/api/http';
import { Page } from '../../../modules/api/pagination';

import { ContestRole } from './contestWeb';

export interface Contest {
  id: number;
  jid: string;
  slug: string;
  name: string;
  style: ContestStyle;
  beginTime: number;
  duration: number;
}

export interface ContestConfig {
  canAdminister: boolean;
}

export interface ContestsResponse {
  data: Page<Contest>;
  rolesMap: Map<string, ContestRole>;
  config: ContestConfig;
}

export interface ActiveContestsResponse {
  data: Contest[];
  rolesMap: Map<string, ContestRole>;
}

export interface ContestCreateData {
  slug: string;
}

export interface ContestUpdateData {
  slug?: string;
  name?: string;
  style?: ContestStyle;
  beginTime?: number;
  duration?: number;
}

export interface ContestDescription {
  description: string;
}

export enum ContestStyle {
  ICPC = 'ICPC',
  IOI = 'IOI',
  GCJ = 'GCJ',
  Bundle = 'BUNDLE',
}

export interface ContestPage extends Page<Contest> {}

export enum ContestErrors {
  SlugAlreadyExists = 'Uriel:ContestSlugAlreadyExists',
  ProblemSlugsNotAllowed = 'Uriel:ContestProblemSlugsNotAllowed',
}

export const baseContestsURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

export function baseContestURL(contestJid: string) {
  return `${baseContestsURL}/${contestJid}`;
}

export const contestAPI = {
  createContest: (token: string, data: ContestCreateData): Promise<Contest> => {
    return post(baseContestsURL, token, data);
  },

  updateContest: (token: string, contestJid: string, data: ContestUpdateData): Promise<Contest> => {
    return post(`${baseContestURL(contestJid)}`, token, data);
  },

  getContests: (token: string, name?: string, page?: number): Promise<ContestsResponse> => {
    const params = stringify({ name, page });
    return get(`${baseContestsURL}?${params}`, token);
  },

  getActiveContests: (token: string): Promise<ActiveContestsResponse> => {
    return get(`${baseContestsURL}/active`, token);
  },

  getContestBySlug: (token: string, contestSlug: string): Promise<Contest> => {
    return get(`${baseContestsURL}/slug/${contestSlug}`, token);
  },

  startVirtualContest: (token: string, contestJid: string): Promise<Contest> => {
    return post(`${baseContestURL(contestJid)}/virtual`, token);
  },

  resetVirtualContest: (token: string, contestJid: string): Promise<void> => {
    return put(`${baseContestURL(contestJid)}/virtual/reset`, token);
  },

  getContestDescription: (token: string, contestJid: string): Promise<ContestDescription> => {
    return get(`${baseContestURL(contestJid)}/description`, token);
  },

  updateContestDescription: (
    token: string,
    contestJid: string,
    description: ContestDescription
  ): Promise<ContestDescription> => {
    return post(`${baseContestURL(contestJid)}/description`, token, description);
  },
};
