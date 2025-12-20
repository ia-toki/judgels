import { useSelector } from 'react-redux';

import { ContestStyle } from '../../../../../../../modules/api/uriel/contest';
import { selectContest } from '../../../../modules/contestSelectors';
import ContestBundleProblemPage from './Bundle/ContestProblemPage';
import ContestProgrammingProblemPage from './Programming/ContestProblemPage';

export default function ContestProblemPage() {
  const contest = useSelector(selectContest);

  if (contest.style === ContestStyle.Bundle) {
    return <ContestBundleProblemPage />;
  }
  return <ContestProgrammingProblemPage />;
}
