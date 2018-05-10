import { Intent } from '@blueprintjs/core';

export enum VerdictCode {
  AC = 'AC',
  WA = 'WA',
  TLE = 'TLE',
  RTE = 'RTE',
  SKP = 'SKP',
}

const verdictIntentsMap = {
  AC: Intent.SUCCESS,
  WA: Intent.DANGER,
  SKP: Intent.NONE,
};

const verdictDisplayCodeMap = {
  SKP: 'SKIPPED',
};

export function getVerdictIntent(code: string): Intent {
  return verdictIntentsMap[code] || Intent.WARNING;
}

export function getVerdictDisplayCode(code: string): Intent {
  return verdictDisplayCodeMap[code] || code;
}
