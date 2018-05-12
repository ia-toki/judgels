import { Intent } from '@blueprintjs/core';

export enum VerdictCode {
  AC = 'AC',
  WA = 'WA',
  TLE = 'TLE',
  RTE = 'RTE',
  PND = '?',
  ERR = '!!!',
  SKP = 'SKP',
}

const verdictIntentsMap = {
  AC: Intent.SUCCESS,
  WA: Intent.DANGER,
  SKP: Intent.NONE,
  PND: Intent.NONE,
  ERR: Intent.NONE,
};

const verdictDisplayCodeMap = {
  PND: '...',
  ERR: 'ERROR!',
  SKP: 'SKIPPED',
};

export function getVerdictIntent(code: string): Intent {
  return verdictIntentsMap[code] || Intent.WARNING;
}

export function getVerdictDisplayCode(code: string): Intent {
  return verdictDisplayCodeMap[code] || code;
}
