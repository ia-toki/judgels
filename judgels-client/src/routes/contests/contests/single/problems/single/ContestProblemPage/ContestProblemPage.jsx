import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContestStyle } from '../../../../../../../modules/api/uriel/contest';
import { contestBySlugQueryOptions } from '../../../../../../../modules/queries/contest';
import ContestBundleProblemPage from './Bundle/ContestProblemPage';
import ContestProgrammingProblemPage from './Programming/ContestProblemPage';

export default function ContestProblemPage() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  if (contest.style === ContestStyle.Bundle) {
    return <ContestBundleProblemPage />;
  }
  return <ContestProgrammingProblemPage />;
}
