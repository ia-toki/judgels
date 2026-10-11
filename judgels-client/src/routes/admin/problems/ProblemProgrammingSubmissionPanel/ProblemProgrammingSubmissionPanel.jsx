import { ChevronLeft } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useQuery } from '@tanstack/react-query';

import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { problemSubmissionWithSourceByIdQueryOptions } from '../../../../modules/queries/problemSubmission';

export default function ProblemProgrammingSubmissionPanel({ problem, submissionId }) {
  const { data: response } = useQuery(problemSubmissionWithSourceByIdQueryOptions(problem.jid, submissionId));

  const renderSubmission = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissionWithSource, profile } = response;

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        profile={profile}
        showLoaderWhenPending
      />
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
