import { Intent } from '@blueprintjs/core';

export enum VerdictCode {
  AC = 'AC',
  CE = 'CE',
  WA = 'WA',
  TLE = 'TLE',
  RTE = 'RTE',
  PND = '?',
  ERR = '!!!',
  SKP = 'SKP',
}

const verdictIntentsMap = {
  [VerdictCode.AC]: Intent.SUCCESS,
  [VerdictCode.WA]: Intent.DANGER,
  [VerdictCode.PND]: Intent.NONE,
  [VerdictCode.ERR]: Intent.NONE,
  [VerdictCode.SKP]: Intent.NONE,
};

const verdictDisplayCodeMap = {
  [VerdictCode.ERR]: 'ERR',
  [VerdictCode.SKP]: 'SKIPPED',
};

export function getVerdictIntent(code: string): Intent {
  return verdictIntentsMap[code] || Intent.WARNING;
}

export function getVerdictDisplayCode(code: string): Intent {
  return verdictDisplayCodeMap[code] || code;
}
