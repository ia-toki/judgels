import { delete_, get, put } from '../http';

import { baseContestURL } from './contest';

export const ContestModuleType = {
  Clarification: 'CLARIFICATION',
  ClarificationTimeLimit: 'CLARIFICATION_TIME_LIMIT',
  Division: 'DIVISION',
  Editorial: 'EDITORIAL',
  File: 'FILE',
  ExternalScoreboard: 'EXTERNAL_SCOREBOARD',
  FrozenScoreboard: 'FROZEN_SCOREBOARD',
  MergedScoreboard: 'MERGED_SCOREBOARD',
  Hidden: 'HIDDEN',
  Paused: 'PAUSE',
  Registration: 'REGISTRATION',
  Scoreboard: 'SCOREBOARD',
  Virtual: 'VIRTUAL',
};

export const moduleTitlesMap = {
  [ContestModuleType.Registration]: 'Registration',
  [ContestModuleType.Clarification]: 'Clarification',
  [ContestModuleType.ClarificationTimeLimit]: 'Clarification time limit',
  [ContestModuleType.Division]: 'Division',
  [ContestModuleType.Editorial]: 'Editorial',
  [ContestModuleType.FrozenScoreboard]: 'Freezable scoreboard',
  [ContestModuleType.MergedScoreboard]: 'Merged scoreboard',
  [ContestModuleType.ExternalScoreboard]: 'External scoreboard',
  [ContestModuleType.Virtual]: 'Virtual contest',
  [ContestModuleType.File]: 'File',
  [ContestModuleType.Paused]: 'Paused',
  [ContestModuleType.Hidden]: 'Hidden',
};

export const moduleDescriptionsMap = {
  [ContestModuleType.Registration]: 'Allows public users to register to the contest.',
  [ContestModuleType.Clarification]: 'Enables clarifications in the contest.',
  [ContestModuleType.ClarificationTimeLimit]: 'Limits how long contestants can make clarifications.',
  [ContestModuleType.Division]: 'Limits registration based on rating.',
  [ContestModuleType.Editorial]: 'Shows problem editorials when contest ends.',
  [ContestModuleType.FrozenScoreboard]: 'Allows the scoreboard to be frozen.',
  [ContestModuleType.MergedScoreboard]: 'Merges scoreboard with a previous contest.',
  [ContestModuleType.ExternalScoreboard]: 'Sends scoreboard updates to an external endpoint.',
  [ContestModuleType.Virtual]: 'Allows contestants to start the contest at their preferred time.',
  [ContestModuleType.File]: 'Allows public files to be uploaded to the contest.',
  [ContestModuleType.Paused]: 'Pauses the contest; contestants cannot submit or make clarifications.',
  [ContestModuleType.Hidden]: 'Hides the contest from contestants and supervisors.',
};

export const allModules = [
  ContestModuleType.Registration,
  ContestModuleType.Clarification,
  ContestModuleType.ClarificationTimeLimit,
  ContestModuleType.Division,
  ContestModuleType.Editorial,
  ContestModuleType.FrozenScoreboard,
  ContestModuleType.MergedScoreboard,
  ContestModuleType.ExternalScoreboard,
  ContestModuleType.Virtual,
  ContestModuleType.File,
  ContestModuleType.Paused,
  ContestModuleType.Hidden,
];

const baseURL = contestJid => `${baseContestURL(contestJid)}/modules`;

export const contestModuleAPI = {
  getModules: (token, contestJid) => {
    return get(`${baseURL(contestJid)}`, token);
  },

  enableModule: (token, contestJid, type) => {
    return put(`${baseURL(contestJid)}/${type}`, token);
  },

  disableModule: (token, contestJid, type) => {
    return delete_(`${baseURL(contestJid)}/${type}`, token);
  },

  getConfig: (token, contestJid) => {
    return get(`${baseURL(contestJid)}/config`, token);
  },

  upsertConfig: (token, contestJid, config) => {
    return put(`${baseURL(contestJid)}/config`, token, config);
  },
};
