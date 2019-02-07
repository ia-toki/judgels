import { ContestProblemStatus } from 'modules/api/uriel/contestProblem';
import { Alias, Slug, NonnegativeNumber } from 'components/forms/validations';

export const GcjValidProblemsSetData = value => {
  const problems = value
    .split('\n')
    .map(s => s.trim())
    .filter(s => s.length > 0)
    .map(s => s.split(','))
    .map(s => s.map(t => t.trim()));

  const aliases: string[] = [];
  const slugs: string[] = [];

  for (const p of problems) {
    if (p.length < 3 || p.length > 5) {
      return 'Each line must contain 3-5 comma-separated elements';
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

    const points = p[2];
    const pointsValidation = NonnegativeNumber(points);
    if (pointsValidation) {
      return 'Problem points: ' + pointsValidation;
    }

    const status = p[3];
    if (status !== undefined) {
      if (status !== ContestProblemStatus.Open && status !== ContestProblemStatus.Closed) {
        return `Problem statuses: Must be either ${ContestProblemStatus.Open} or ${ContestProblemStatus.Closed}`;
      }
    }

    const submissionsLimit = p[4];
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
};
