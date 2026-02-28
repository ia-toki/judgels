import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../../../components/Pagination/Pagination';
import { RegradeAllButton } from '../../../../../../../../components/RegradeAllButton/RegradeAllButton';
import SubmissionUserFilter from '../../../../../../../../components/SubmissionUserFilter/SubmissionUserFilter';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
} from '../../../../../../../../modules/queries/problemSet';
import {
  problemSetProgrammingSubmissionsQueryOptions,
  regradeProblemSetProgrammingSubmissionMutationOptions,
  regradeProblemSetProgrammingSubmissionsMutationOptions,
} from '../../../../../../../../modules/queries/problemSetSubmissionProgramming';
import { useSession } from '../../../../../../../../modules/session';
import { reallyConfirm } from '../../../../../../../../utils/confirmation';
import { ProblemSubmissionsTable } from '../ProblemSubmissionsTable/ProblemSubmissionsTable';

import * as toastActions from '../../../../../../../../modules/toast/toastActions';

const PAGE_SIZE = 20;

export default function ProblemSubmissionsPage() {
  const { problemSetSlug, problemAlias } = useParams({ strict: false });
  const location = useLocation();
  const { user } = useSession();
  const userJid = user?.jid;
  const username = user?.username;
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(problemSet.jid, problemAlias));

  const page = +(location.search.page || 1);
  const isUserFilterMine = (location.pathname + '/').includes('/mine/');
  const usernameFilter = isUserFilterMine ? username : undefined;

  const { data: response } = useQuery(
    problemSetProgrammingSubmissionsQueryOptions(problem.problemJid, { username: usernameFilter, page })
  );

  const regradeSubmissionMutation = useMutation(
    regradeProblemSetProgrammingSubmissionMutationOptions(problem.problemJid)
  );
  const regradeSubmissionsMutation = useMutation(
    regradeProblemSetProgrammingSubmissionsMutationOptions(problem.problemJid)
  );

  const onRegrade = async submissionJid => {
    await regradeSubmissionMutation.mutateAsync(submissionJid, {
      onSuccess: () => {
        toastActions.showSuccessToast('Regrade in progress.');
      },
    });
  };

  const onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages?')) {
      await regradeSubmissionsMutation.mutateAsync(undefined, {
        onSuccess: () => {
          toastActions.showSuccessToast('Regrade in progress.');
        },
      });
    }
  };

  const renderUserFilter = () => {
    return userJid && <SubmissionUserFilter />;
  };

  const renderHeader = () => {
    return <div className="content-card__header">{renderRegradeAllButton()}</div>;
  };

  const renderRegradeAllButton = () => {
    if (!response || !response.config.canManage) {
      return null;
    }
    return <RegradeAllButton onRegradeAll={onRegradeAll} />;
  };

  const renderSubmissions = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissions, config, profilesMap, problemAliasesMap } = response;
    if (submissions.totalCount === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <ProblemSubmissionsTable
        problemSet={problemSet}
        problem={problem}
        submissions={submissions.page}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        onRegrade={onRegrade}
      />
    );
  };

  return (
    <ContentCard>
      <h3>Submissions</h3>
      <hr />
      {renderUserFilter()}
      {renderHeader()}
      {renderSubmissions()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
