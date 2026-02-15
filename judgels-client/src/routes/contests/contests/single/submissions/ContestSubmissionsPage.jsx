import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContestStyle } from '../../../../../modules/api/uriel/contest';
import { ContestRole } from '../../../../../modules/api/uriel/contestWeb';
import { contestBySlugQueryOptions } from '../../../../../modules/queries/contest';
import { contestWebConfigQueryOptions } from '../../../../../modules/queries/contestWeb';
import { useSession } from '../../../../../modules/session';
import BundleContestSubmissionSummaryPage from './Bundle/ContestSubmissionSummaryPage/ContestSubmissionSummaryPage';
import BundleContestSubmissionsPage from './Bundle/ContestSubmissionsPage/ContestSubmissionsPage';
import ProgrammingContestSubmissionsPage from './Programming/ContestSubmissionsPage/ContestSubmissionsPage';

export default function ContestSubmissionsPage() {
  const { contestSlug } = useParams({ strict: false });
  const { token } = useSession();
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const { data: webConfig } = useSuspenseQuery(contestWebConfigQueryOptions(token, contestSlug));

  if (contest.style === ContestStyle.Bundle) {
    if (webConfig.role === ContestRole.Contestant) {
      return <BundleContestSubmissionSummaryPage />;
    }
    return <BundleContestSubmissionsPage />;
  }
  return <ProgrammingContestSubmissionsPage />;
}
