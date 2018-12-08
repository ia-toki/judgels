import { delete_, get, put } from 'modules/api/http';
import { LanguageRestriction } from 'modules/api/gabriel/language';

import { baseContestURL } from './contest';

export enum ContestModuleType {
  Clarification = 'CLARIFICATION',
  ClarificationTimeLimit = 'CLARIFICATION_TIME_LIMIT',
  DelayedGrading = 'DELAYED_GRADING',
  File = 'FILE',
  FrozenScoreboard = 'FROZEN_SCOREBOARD',
  Paused = 'PAUSE',
  Registration = 'REGISTRATION',
  Scoreboard = 'SCOREBOARD',
  Virtual = 'VIRTUAL',
}

export const moduleTitlesMap = {
  [ContestModuleType.Registration]: 'Registration',
  [ContestModuleType.Clarification]: 'Clarification',
  [ContestModuleType.ClarificationTimeLimit]: 'Clarification time limit',
  [ContestModuleType.FrozenScoreboard]: 'Freezable scoreboard',
  [ContestModuleType.Virtual]: 'Virtual contest',
  [ContestModuleType.DelayedGrading]: 'Delayed grading',
  [ContestModuleType.File]: 'File',
  [ContestModuleType.Paused]: 'Paused',
};

export const moduleDescriptionsMap = {
  [ContestModuleType.Registration]: 'Allows public users to register to the contest.',
  [ContestModuleType.Clarification]: 'Enables clarifications in the contest.',
  [ContestModuleType.ClarificationTimeLimit]: 'Limits how long contestants can make clarifications.',
  [ContestModuleType.FrozenScoreboard]: 'Allows the scoreboard to be frozen.',
  [ContestModuleType.Virtual]: 'Allows contestants to start the contest at their preferred time.',
  [ContestModuleType.DelayedGrading]: 'Delay grading of submissions until a specified duration.',
  [ContestModuleType.File]: 'Allows public files to be uploaded to the contest.',
  [ContestModuleType.Paused]: 'Pauses the contest; contestants cannot submit or make clarifications.',
};

export const allModules: ContestModuleType[] = [
  ContestModuleType.Registration,
  ContestModuleType.Clarification,
  ContestModuleType.ClarificationTimeLimit,
  ContestModuleType.DelayedGrading,
  ContestModuleType.FrozenScoreboard,
  ContestModuleType.Virtual,
  ContestModuleType.File,
  ContestModuleType.Paused,
];

export interface IcpcStyleModuleConfig {
  languageRestriction: LanguageRestriction;
  wrongSubmissionPenalty: number;
}

export interface IoiStyleModuleConfig {
  languageRestriction: LanguageRestriction;
  usingLastAffectingPenalty: boolean;
}

export interface ClarificationTimeLimitModuleConfig {
  clarificationDuration: number;
}

export interface DelayedGradingModuleConfig {
  delayDuration: number;
}

export interface FrozenScoreboardModuleConfig {
  isOfficialScoreboardAllowed: boolean;
  scoreboardFreezeTime: number; // freezeDurationBeforeEndTime
}

export interface ScoreboardModuleConfig {
  isIncognitoScoreboard: boolean;
}

export interface VirtualModuleConfig {
  virtualDuration: number;
}

export interface ContestModulesConfig {
  icpcStyle?: IcpcStyleModuleConfig;
  ioiStyle?: IoiStyleModuleConfig;

  scoreboard: ScoreboardModuleConfig;

  clarificationTimeLimit?: ClarificationTimeLimitModuleConfig;
  delayedGrading?: DelayedGradingModuleConfig;
  frozenScoreboard?: FrozenScoreboardModuleConfig;
  virtual?: VirtualModuleConfig;
}

const baseURL = (contestJid: string) => `${baseContestURL(contestJid)}/modules`;

export const contestModuleAPI = {
  getModules: (token: string, contestJid: string): Promise<ContestModuleType[]> => {
    return get(`${baseURL(contestJid)}`, token);
  },

  enableModule: (token: string, contestJid: string, type: ContestModuleType): Promise<void> => {
    return put(`${baseURL(contestJid)}/${type}`, token);
  },

  disableModule: (token: string, contestJid: string, type: ContestModuleType): Promise<void> => {
    return delete_(`${baseURL(contestJid)}/${type}`, token);
  },

  getConfig: (token: string, contestJid: string): Promise<ContestModulesConfig> => {
    return get(`${baseURL(contestJid)}/config`, token);
  },

  upsertConfig: (token: string, contestJid: string, config: ContestModulesConfig): Promise<void> => {
    return put(`${baseURL(contestJid)}/config`, token, config);
  },
};
