import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ProblemType } from '../../../../modules/api/problem';
import { problemBySlugQueryOptions, problemQueryOptions } from '../../../../modules/queries/problem';
import ProblemBundleSubmissionsPanel from '../ProblemBundleSubmissionsPanel/ProblemBundleSubmissionsPanel';
import ProblemProgrammingSubmissionsPanel from '../ProblemProgrammingSubmissionsPanel/ProblemProgrammingSubmissionsPanel';

export default function ProblemSubmissionsPage() {
  const { problemSlug } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const { data: problem } = useSuspenseQuery(problemQueryOptions(problemJid));

  if (problem.type === ProblemType.Bundle) {
    return <ProblemBundleSubmissionsPanel problem={problem} />;
  }
  return <ProblemProgrammingSubmissionsPanel problem={problem} />;
}
