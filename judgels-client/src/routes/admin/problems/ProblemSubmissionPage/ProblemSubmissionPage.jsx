import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ProblemType } from '../../../../modules/api/problem';
import { problemBySlugQueryOptions, problemQueryOptions } from '../../../../modules/queries/problem';
import ProblemBundleSubmissionPanel from '../ProblemBundleSubmissionPanel/ProblemBundleSubmissionPanel';
import ProblemProgrammingSubmissionPanel from '../ProblemProgrammingSubmissionPanel/ProblemProgrammingSubmissionPanel';

export default function ProblemSubmissionPage() {
  const { problemSlug, submissionId } = useParams({ strict: false });

  const {
    data: { jid: problemJid },
  } = useSuspenseQuery(problemBySlugQueryOptions(problemSlug));

  const { data: problem } = useSuspenseQuery(problemQueryOptions(problemJid));

  if (problem.type === ProblemType.Bundle) {
    return <ProblemBundleSubmissionPanel problem={problem} submissionId={+submissionId} />;
  }
  return <ProblemProgrammingSubmissionPanel problem={problem} submissionId={+submissionId} />;
}
