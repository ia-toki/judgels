import { ChevronLeft } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useQuery } from '@tanstack/react-query';

import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { FormattedDate } from '../../../../components/FormattedDate/FormattedDate';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../components/SubmissionDetails/Bundle/SubmissionDetails/SubmissionDetails';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { ItemType } from '../../../../modules/api/problemBundle';
import {
  problemBundleSubmissionWithAnswerByIdQueryOptions,
  regradeProblemBundleSubmissionMutationOptions,
} from '../../../../modules/queries/problemBundleSubmission';
import { problemItemsQueryOptions } from '../../../../modules/queries/problemItem';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ProblemBundleSubmissionPanel({ problem, submissionId }) {
  const { data: response } = useQuery(problemBundleSubmissionWithAnswerByIdQueryOptions(problem.jid, submissionId));
  const { data: itemsResponse } = useQuery(problemItemsQueryOptions(problem.jid));

  const regradeSubmissionMutation = useMutation(regradeProblemBundleSubmissionMutationOptions(problem.jid));

  const renderSubmission = () => {
    if (!response || !itemsResponse) {
      return <LoadingState />;
    }

    const { data: submission, answer, profile } = response;
    const items = itemsResponse.data.filter(item => item.type !== ItemType.Statement);

    // A bundle submission answers the whole problem at once, and is graded per item.
    const submissionsByItemJid = {};
    Object.keys(answer.answers).forEach(itemJid => {
      const grading = submission.latestGrading.details[itemJid];
      submissionsByItemJid[itemJid] = {
        answer: answer.answers[itemJid],
        grading: grading && { score: grading.score },
      };
    });

    const regradeSubmission = () => {
      regradeSubmissionMutation.mutate(submission.jid, {
        onSuccess: () => toastActions.showSuccessToast('Submission regraded.'),
      });
    };

    return (
      <>
        <p>
          <UserRef profile={profile} /> &bull; <FormattedDate value={submission.time} showSeconds />
        </p>
        <SubmissionDetails
          showTitle={false}
          itemJids={items.map(item => item.jid)}
          submissionsByItemJid={submissionsByItemJid}
          itemTypesMap={Object.fromEntries(items.map(item => [item.jid, item.type]))}
          canViewGrading
          showVerdict={false}
          canManage
          onRegrade={regradeSubmission}
        />
      </>
    );
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Submission #{submissionId}</span>
          <ButtonLink small icon={<ChevronLeft />} to={`/admin/problems/${problem.slug}/submissions`}>
            Back
          </ButtonLink>
        </h4>
      </Flex>
      {renderSubmission()}
    </div>
  );
}
