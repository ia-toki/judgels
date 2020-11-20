import { Intent } from '@blueprintjs/core';

export const VerdictCode = {
  AC: 'AC',
  CE: 'CE',
  WA: 'WA',
  TLE: 'TLE',
  RTE: 'RTE',
  PND: '?',
  ERR: '!!!',
  SKP: 'SKP',
};

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

export function getVerdictIntent(code) {
  return verdictIntentsMap[code] || Intent.WARNING;
}

export function getVerdictDisplayCode(code) {
  return verdictDisplayCodeMap[code] || code;
}
