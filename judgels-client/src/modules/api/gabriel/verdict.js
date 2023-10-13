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
  [VerdictCode.CE]: Intent.NONE,
  [VerdictCode.PND]: Intent.NONE,
  [VerdictCode.ERR]: Intent.NONE,
  [VerdictCode.SKP]: Intent.NONE,
};

const verdictDisplayNamesMap = {
  [VerdictCode.AC]: 'Accepted',
  [VerdictCode.WA]: 'Wrong Answer',
  [VerdictCode.TLE]: 'Time Limit Exceeded',
  [VerdictCode.RTE]: 'Runtime Error',
  [VerdictCode.CE]: 'Compilation Error',
  [VerdictCode.PND]: 'Pending',
  [VerdictCode.ERR]: 'Internal Error',
  [VerdictCode.SKP]: 'Skipped',
};

export function getVerdictIntent(code) {
  return verdictIntentsMap[code] || Intent.WARNING;
}

export function getVerdictDisplayName(code) {
  return verdictDisplayNamesMap[code] || code;
}
