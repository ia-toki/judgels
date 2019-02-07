import { ContestProblemData } from 'modules/api/uriel/contestProblem';
import { ContestProblemStatus } from 'modules/api/uriel/contestProblem';

import { ContestProblemProcessor } from '../contestProblemProcessor';

export const GcjContestProblemProcessor: ContestProblemProcessor = {
  toString: (problems: ContestProblemData[]) => {
    return problems
      .map(p => {
        const points = p.points || 0;
        if (p.submissionsLimit > 0) {
          return `${p.alias},${p.slug},${points},${p.status},${p.submissionsLimit}`;
        } else if (p.status !== ContestProblemStatus.Open) {
          return `${p.alias},${p.slug},${points},${p.status}`;
        } else {
          return `${p.alias},${p.slug},${points}`;
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
            points: +s[2],
            status: s[3] || ContestProblemStatus.Open,
            submissionsLimit: +s[4] || 0,
          } as ContestProblemData)
      );
  },
};
