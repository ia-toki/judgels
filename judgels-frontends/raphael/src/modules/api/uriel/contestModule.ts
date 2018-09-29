import { APP_CONFIG } from 'conf';

import { delete_, get, put } from '../http';

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
  [ContestModuleType.Virtual]: 'Virtual',
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
  ContestModuleType.FrozenScoreboard,
  ContestModuleType.Virtual,
  ContestModuleType.File,
  ContestModuleType.Paused,
];

export function createContestModuleAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getModules: (token: string, contestJid: string): Promise<ContestModuleType[]> => {
      return get(`${baseURL}/${contestJid}/modules`, token);
    },

    enableModule: (token: string, contestJid: string, type: ContestModuleType): Promise<void> => {
      return put(`${baseURL}/${contestJid}/modules/${type}`, token);
    },

    disableModule: (token: string, contestJid: string, type: ContestModuleType): Promise<void> => {
      return delete_(`${baseURL}/${contestJid}/modules/${type}`, token);
    },
  };
}
