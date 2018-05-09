import { Intent } from '@blueprintjs/core';

const verdictIntentsMap = {
  AC: Intent.SUCCESS,
  WA: Intent.DANGER,
};

export function getVerdictIntent(code: string): Intent {
  return verdictIntentsMap[code] || Intent.WARNING;
}
