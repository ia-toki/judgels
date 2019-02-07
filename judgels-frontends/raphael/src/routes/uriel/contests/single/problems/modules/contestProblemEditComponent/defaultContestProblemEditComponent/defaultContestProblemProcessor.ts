import { ContestProblemData } from 'modules/api/uriel/contestProblem';
import { ContestProblemStatus } from 'modules/api/uriel/contestProblem';

import { ContestProblemProcessor } from '../contestProblemProcessor';

export const DefaultContestProblemProcessor: ContestProblemProcessor = {
  toString: (problems: ContestProblemData[]) => {
    return problems
      .map(p => {
        if (p.submissionsLimit > 0) {
          return `${p.alias},${p.slug},${p.status},${p.submissionsLimit}`;
        } else if (p.status !== ContestProblemStatus.Open) {
          return `${p.alias},${p.slug},${p.status}`;
        } else {
          return `${p.alias},${p.slug}`;
        }
      })
      .join('\n');
  },

  toContestProblemData: (problems: string) => {
    return problems
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()))
      .map(
        s =>
          ({
            alias: s[0],
            slug: s[1],
            status: s[2] || ContestProblemStatus.Open,
            submissionsLimit: +s[3] || 0,
            points: 0,
          } as ContestProblemData)
      );
  },
};
