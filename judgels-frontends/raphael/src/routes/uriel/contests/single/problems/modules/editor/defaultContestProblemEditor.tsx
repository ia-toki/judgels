import * as React from 'react';

import { ContestProblemData, ContestProblemStatus } from 'modules/api/uriel/contestProblem';
import { Alias, Slug, NonnegativeNumber } from 'components/forms/validations';

import { ContestProblemEditor } from './contestProblemEditor';

export default {
  validator: value => {
    const problems = value
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()));

    const aliases: string[] = [];
    const slugs: string[] = [];

    for (const p of problems) {
      if (p.length < 2 || p.length > 4) {
        return 'Each line must contain 2-4 comma-separated elements';
      }
      const alias = p[0];
      const aliasValidation = Alias(alias);
      if (aliasValidation) {
        return 'Problem aliases: ' + aliasValidation;
      }

      const slug = p[1];
      const slugValidation = Slug(slug);
      if (slugValidation) {
        return 'Problem slugs: ' + slugValidation;
      }

      const status = p[2];
      if (status !== undefined) {
        if (status !== ContestProblemStatus.Open && status !== ContestProblemStatus.Closed) {
          return `Problem statuses: Must be either ${ContestProblemStatus.Open} or ${ContestProblemStatus.Closed}`;
        }
      }

      const submissionsLimit = p[3];
      if (submissionsLimit !== undefined) {
        const submissionsLimitValidation = NonnegativeNumber(submissionsLimit);
        if (submissionsLimitValidation) {
          return 'Problem submissions limits: ' + submissionsLimitValidation;
        }
      }

      aliases.push(alias);
      slugs.push(slug);
    }

    if (new Set(aliases).size !== aliases.length) {
      return 'Problem aliases must be unique';
    }
    if (new Set(slugs).size !== slugs.length) {
      return 'Problem slugs must be unique';
    }

    return undefined;
  },
  serializer: (problems: ContestProblemData[]) => {
    return problems
      .map(p => {
        if (p.submissionsLimit !== undefined) {
          return `${p.alias},${p.slug},${p.status},${p.submissionsLimit}`;
        } else if (p.status !== ContestProblemStatus.Open) {
          return `${p.alias},${p.slug},${p.status}`;
        } else {
          return `${p.alias},${p.slug}`;
        }
      })
      .join('\n');
  },
  deserializer: (problems: string) => {
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
            submissionsLimit: s[3] && +s[3],
            points: undefined,
          } as ContestProblemData)
      );
  },
  format: <code>alias,slug[,status[,submissionsLimit]]</code>,
  example: <pre>{'A,hello\nB,tree,CLOSED\nC,flow,OPEN,20'}</pre>,
} as ContestProblemEditor;
