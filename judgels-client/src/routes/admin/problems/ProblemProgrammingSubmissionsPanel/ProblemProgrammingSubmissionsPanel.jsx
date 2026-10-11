import { HTMLTable } from '@blueprintjs/core';
import { Refresh, Search } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { Link, useLocation } from '@tanstack/react-router';

import { FormattedRelative } from '../../../../components/FormattedRelative/FormattedRelative';
import { GradingVerdictTag } from '../../../../components/GradingVerdictTag/GradingVerdictTag';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../components/Pagination/Pagination';
import { RegradeAllButton } from '../../../../components/RegradeAllButton/RegradeAllButton';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { getGradingLanguageName } from '../../../../modules/api/gradingLanguage';
import { problemPermissionsQueryOptions } from '../../../../modules/queries/problem';
import {
  problemSubmissionsQueryOptions,
  regradeProblemSubmissionMutationOptions,
  regradeProblemSubmissionsMutationOptions,
} from '../../../../modules/queries/problemSubmission';
import { reallyConfirm } from '../../../../utils/confirmation';

import * as toastActions from '../../../../modules/toast/toastActions';

import '../../../../components/SubmissionsTable/Programming/SubmissionsTable.scss';

const PAGE_SIZE = 20;

export default function ProblemProgrammingSubmissionsPanel({ problem }) {
  const location = useLocation();
  const page = location.search.page;

  const {
    data: { canEdit },
  } = useSuspenseQuery(problemPermissionsQueryOptions(problem.jid));

  const { data: response } = useQuery(problemSubmissionsQueryOptions(problem.jid, { page }));

  const regradeSubmissionMutation = useMutation(regradeProblemSubmissionMutationOptions(problem.jid));
  const regradeSubmissionsMutation = useMutation(regradeProblemSubmissionsMutationOptions(problem.jid));

  const regradeSubmission = submissionJid => {
    regradeSubmissionMutation.mutate(submissionJid, {
      onSuccess: () => toastActions.showSuccessToast('Regrade in progress.'),
    });
  };

  const regradeSubmissions = () => {
    if (reallyConfirm('Regrade all submissions in all pages?')) {
      regradeSubmissionsMutation.mutate(undefined, {
        onSuccess: () => toastActions.showSuccessToast('Regrade in progress.'),
      });
    }
  };

  const renderSubmissions = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissions, profilesMap } = response;
    if (submissions.page.length === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <HTMLTable striped className="table-list-condensed submissions-table">
        <thead>
          <tr>
            <th className="col-fit">ID</th>
            <th>User</th>
            <th className="col-fit">Lang</th>
            <th className="col-fit">Verdict</th>
            <th>Time</th>
            <th className="col-fit" />
          </tr>
        </thead>
        <tbody>
          {submissions.page.map(submission => (
            <tr key={submission.jid}>
              <td className="col-fit">
                {submission.id}
                {canEdit && (
                  <>
                    &nbsp;&nbsp;&nbsp;
                    <Refresh
                      className="action"
                      intent="primary"
                      title={`Regrade submission ${submission.id}`}
                      onClick={() => regradeSubmission(submission.jid)}
                    />
                  </>
                )}
              </td>
              <td>
                <UserRef profile={profilesMap[submission.userJid]} />
              </td>
              <td className="col-fit">{getGradingLanguageName(submission.gradingLanguage)}</td>
              <td className="col-fit">
                {submission.latestGrading && <GradingVerdictTag wide grading={submission.latestGrading} />}
              </td>
              <td>
                <FormattedRelative value={submission.time} />
              </td>
              <td className="col-fit">
                <Link className="action" to={`/admin/problems/${problem.slug}/submissions/${submission.id}`}>
                  <Search title={`View submission ${submission.id}`} />
                </Link>
              </td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
    );
  };

  return (
    <Flex flexDirection="column" gap={2}>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Submissions</span>
          {canEdit && <RegradeAllButton onRegradeAll={regradeSubmissions} />}
        </h4>
      </Flex>
      {renderSubmissions()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </Flex>
  );
}
