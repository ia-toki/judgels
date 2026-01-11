import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useSelector } from 'react-redux';

import { ContestStyle } from '../../../../../../../modules/api/uriel/contest';
import { contestBySlugQueryOptions } from '../../../../../../../modules/queries/contest';
import { selectToken } from '../../../../../../../modules/session/sessionSelectors';
import ContestBundleProblemPage from './Bundle/ContestProblemPage';
import ContestProgrammingProblemPage from './Programming/ContestProblemPage';

export default function ContestProblemPage() {
  const { contestSlug } = useParams({ strict: false });
  const token = useSelector(selectToken);
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));

  if (contest.style === ContestStyle.Bundle) {
    return <ContestBundleProblemPage />;
  }
  return <ContestProgrammingProblemPage />;
}
