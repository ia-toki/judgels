import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContestStyle } from '../../../../../modules/api/contest';
import { contestBySlugQueryOptions } from '../../../../../modules/queries/contest';
import BundleContestProblemPage from './Bundle/ContestProblemPage/ContestProblemPage';
import ProgrammingContestProblemPage from './Programming/ContestProblemPage/ContestProblemPage';

export default function ContestProblemPage() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  if (contest.style === ContestStyle.Bundle) {
    return <BundleContestProblemPage />;
  }
  return <ProgrammingContestProblemPage />;
}
